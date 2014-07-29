package io.relayr.core.ble;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.os.Build;
import android.util.Log;

import java.util.List;
import java.util.UUID;

import io.relayr.core.ble.device.DeviceCharacteristic;
import io.relayr.core.ble.device.Relayr_BLEDevice;
import io.relayr.core.ble.device.Relayr_BLEDeviceMode;
import io.relayr.core.ble.device.Relayr_BLEDeviceStatus;
import io.relayr.core.ble.device.ShortUUID;

import static io.relayr.core.ble.Relayr_BleUtils.*;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class Relayr_BleGattCallback extends BluetoothGattCallback {

    private static final String TAG = Relayr_BleGattCallback.class.toString();

	private final Relayr_BLEDevice mDevice;
	private static final UUID RELAYR_NOTIFICATION_CHARACTERISTIC =
            UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

	public Relayr_BleGattCallback(Relayr_BLEDevice device) {
		mDevice = device;
	}

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
    	Log.d(TAG, "onConnectionStateChange");
    	if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_CONNECTED) {
    		Log.d(TAG, "Device " + mDevice.getName() + " connected");
    		if (mDevice.getStatus() != Relayr_BLEDeviceStatus.CONFIGURING) {
    			mDevice.setStatus(Relayr_BLEDeviceStatus.CONNECTED);
    			if (mDevice.connectionCallback != null) {
        			Log.d(TAG, "Callback detected: sending onConnect event to " + mDevice.getName());
        			mDevice.connectionCallback.onConnect(mDevice);
        		} else {
        			Log.d(TAG, "Callback not detected: not sending onConnect event to " + mDevice.getName());
        		}
    		} else {
    			gatt.discoverServices();
    		}
    	} else {
    		if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_DISCONNECTED) {
    			if (mDevice.getStatus() != Relayr_BLEDeviceStatus.CONFIGURING) {
    				mDevice.setStatus(Relayr_BLEDeviceStatus.DISCONNECTED);
        			if (mDevice.connectionCallback != null) {
        				Log.d(TAG, "Callback detected: sending onDisconnect event to " + mDevice.getName());
            			mDevice.connectionCallback.onDisconnect(mDevice);
            		} else {
            			Log.d(TAG, "Callback not detected: not sending onDisconnect event to " + mDevice.getName());
            		}
    			} else {
    				mDevice.setStatus(Relayr_BLEDeviceStatus.DISCONNECTED);
    				Log.d(TAG, "Device " + mDevice.getName() + " configured");
    				if (!Relayr_BleListener.discoveredDevices.isFullyConfigured(mDevice.getAddress())) {
        				Relayr_BleListener.discoveredDevices.addNewDevice(mDevice.getAddress(), mDevice);
        				Log.d(TAG, "Device " + mDevice.getName() + " added to discovered devices");
    					Relayr_BleListener.discoveredDevices.notifyDiscoveredDevice(mDevice);
    				}
    			}
    			Log.d(TAG, "Device disconnected");
    		} else {
    			if (isFailureStatus(status)) {
    				if (mDevice.getMode() == Relayr_BLEDeviceMode.UNKNOWN) {
    					Log.d(TAG, "Device " + mDevice.getName() + ": unhandled state change without configuration: " + gattStatusToString(status));
    					Relayr_BleListener.discoveredDevices.removeDevice(mDevice);
    					Log.d(TAG, "Device " + mDevice.getName() + ": removed because error in configuration process");
    					gatt.close();
    					if (mDevice.connectionCallback != null) {
    						mDevice.connectionCallback.onError(mDevice, gattStatusToString(status));
    					}
    				} else {
    					Log.d(TAG, "Device " + mDevice.getName() + ": unhandled state change configured: " + gattStatusToString(status));
    				}
    			} else {
    				Log.d(TAG, "Device " + mDevice.getName() + ": unhandled state change: " + gattStatusToString(status));
    			}
    		}
    	}
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
    	List<BluetoothGattService> services = gatt.getServices();
    	for (BluetoothGattService service:services) {
    		String serviceUUID = getShortUUID(service.getUuid().toString());
    		Log.d(TAG, "Discovered service on device " + mDevice.getName() + ": " + serviceUUID);
			if (serviceUUID.equals(ShortUUID.MODE_DIRECT_CONNECTION)) {
    			setupDeviceForDirectConnectionMode(service, gatt);
    			mDevice.setMode(Relayr_BLEDeviceMode.DIRECTCONNECTION);
    			if (mDevice.getStatus() == Relayr_BLEDeviceStatus.CONFIGURING) {
    				mDevice.disconnect();
    			}
    			break;
    		}
    		if (serviceUUID.equals(ShortUUID.MODE_ON_BOARDING)) {
    			setupDeviceForOnBoardingConnectionMode(service, gatt);
    			mDevice.setMode(Relayr_BLEDeviceMode.ONBOARDING);
    			mDevice.setStatus(Relayr_BLEDeviceStatus.CONNECTED);
    			if (!Relayr_BleListener.discoveredDevices.isFullyConfigured(mDevice.getAddress())) {
    				Relayr_BleListener.discoveredDevices.addNewDevice(mDevice.getAddress(), mDevice);
					Relayr_BleListener.discoveredDevices.notifyDiscoveredDevice(mDevice);
				}
    			break;
    		}
    	}
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        mDevice.setValue(characteristic.getValue());
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
    	Log.d(TAG, "Characteristic wrote on device " + mDevice.getName() + ": " + characteristic.getUuid());
    	Log.d(TAG, "Characteristic wrote status on device " + mDevice.getName() + ": " + gattStatusToString(status));
    	switch (status) {
    	case BluetoothGatt.GATT_SUCCESS: {
    		if (mDevice.connectionCallback != null) {
    			mDevice.connectionCallback.onWriteSucess(mDevice, DeviceCharacteristic.from(characteristic));
    		}
    		break;
    	}
    	default: {
    		if (mDevice.connectionCallback != null) {
    			mDevice.connectionCallback.onWriteError(mDevice, DeviceCharacteristic.from(characteristic), status);
    		}
    		break;
    	}
    	}
    }



    private String gattStatusToString(int status) {
    	switch (status) {
    	case BluetoothGatt.GATT_FAILURE: {
    		return "Failure";
    	}
    	case BluetoothGatt.GATT_INSUFFICIENT_AUTHENTICATION: {
    		return "Insufficient authentication for a given operation";
    	}
    	case BluetoothGatt.GATT_INSUFFICIENT_ENCRYPTION: {
    		return "Insufficient encryption for a given operation";
    	}
    	case BluetoothGatt.GATT_INVALID_ATTRIBUTE_LENGTH: {
    		return "A write operation exceeds the maximum length of the attribute";
    	}
    	case BluetoothGatt.GATT_INVALID_OFFSET: {
    		return "A read or write operation was requested with an invalid offset";
    	}
    	case BluetoothGatt.GATT_READ_NOT_PERMITTED: {
    		return "GATT read operation is not permitted";
    	}
    	case BluetoothGatt.GATT_REQUEST_NOT_SUPPORTED: {
    		return "The given request is not supported";
    	}
    	case BluetoothGatt.GATT_SUCCESS: {
    		return "A GATT operation completed successfully";
    	}
    	case BluetoothGatt.GATT_WRITE_NOT_PERMITTED: {
    		return "GATT write operation is not permitted";
    	}
    	default: return "Not identified error";
    	}
    }

    private boolean isFailureStatus(int status) {
    	switch (status) {
    	case BluetoothGatt.GATT_FAILURE:
    	case BluetoothGatt.GATT_INSUFFICIENT_AUTHENTICATION:
    	case BluetoothGatt.GATT_INSUFFICIENT_ENCRYPTION:
    	case BluetoothGatt.GATT_INVALID_ATTRIBUTE_LENGTH:
    	case BluetoothGatt.GATT_INVALID_OFFSET:
    	case BluetoothGatt.GATT_READ_NOT_PERMITTED:
    	case BluetoothGatt.GATT_REQUEST_NOT_SUPPORTED:
    	case BluetoothGatt.GATT_WRITE_NOT_PERMITTED:
    		return true;
    	case BluetoothGatt.GATT_SUCCESS:
    	default: return false;
    	}
    }

    private void setupDeviceForDirectConnectionMode(BluetoothGattService service, BluetoothGatt gatt) {
    	mDevice.currentService = service;
    	Log.d(TAG, "New mode on device " + mDevice.getName() + ": Direct connection");
    	List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
    	for (BluetoothGattCharacteristic characteristic:characteristics) {
    		String characteristicUUID = getShortUUID(characteristic.getUuid().toString());
    		if (characteristicUUID.equals(ShortUUID.CHARACTERISTIC_DATA_READ)) {
    			gatt.setCharacteristicNotification(characteristic, true);
    			BluetoothGattDescriptor descriptor = characteristic.getDescriptor(RELAYR_NOTIFICATION_CHARACTERISTIC);
    			descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
    			gatt.writeDescriptor(descriptor);
    		}
    	}
    }

    private void setupDeviceForOnBoardingConnectionMode(BluetoothGattService service, BluetoothGatt gatt) {
    	mDevice.currentService = service;
    	Log.d(TAG, "Device new mode on device " + mDevice.getName() + ": on boarding");
    }
}
