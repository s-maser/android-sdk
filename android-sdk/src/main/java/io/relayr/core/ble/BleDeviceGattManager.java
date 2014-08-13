package io.relayr.core.ble;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.os.Build;
import android.util.Log;

import java.util.List;
import java.util.UUID;

import static android.bluetooth.BluetoothProfile.STATE_CONNECTED;
import static android.bluetooth.BluetoothProfile.STATE_DISCONNECTED;
import static io.relayr.core.ble.BleUtils.getShortUUID;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
class BleDeviceGattManager extends BluetoothGattCallback {

    private static final String TAG = BleDeviceGattManager.class.toString();

	private final BleDevice mDevice;
	private static final UUID RELAYR_NOTIFICATION_CHARACTERISTIC =
            UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    private final BleDeviceEventCallback mBleDeviceEventCallback;

    BleDeviceGattManager(BleDevice device, BleDeviceEventCallback eventCallback) {
		mDevice = device;
        mBleDeviceEventCallback = eventCallback;
	}

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
    	Log.d(TAG, "onConnectionStateChange");
    	if (status == BluetoothGatt.GATT_SUCCESS && newState == STATE_CONNECTED) {
    		Log.d(TAG, "Device " + mDevice.getName() + " connected");
    		if (mDevice.getStatus() != BleDeviceStatus.CONFIGURING) {
    			mDevice.onConnect();
    		} else {
    			gatt.discoverServices();
    		}
    	} else {
    		if (status == BluetoothGatt.GATT_SUCCESS && newState == STATE_DISCONNECTED) {
    			if (mDevice.getStatus() != BleDeviceStatus.CONFIGURING) {
            		mDevice.onDisconnect();
    			} else {
    				mDevice.setStatus(BleDeviceStatus.DISCONNECTED);
    				Log.d(TAG, "Device " + mDevice.getName() + " configured");
                    mBleDeviceEventCallback.onDeviceDiscovered(mDevice);
    			}
    			Log.d(TAG, "Device disconnected");
    		} else {
    			if (isFailureStatus(status)) {
    				if (mDevice.getMode() == BleDeviceMode.CONNECTED_TO_MASTER_MODULE) {
    					Log.d(TAG, "Device " + mDevice.getName() + ": unhandled state change without configuration: " + gattStatusToString(status));
    					Log.d(TAG, "Device " + mDevice.getName() + ": removed because error in configuration process");
                        mBleDeviceEventCallback.onDeviceConnectedToMasterModuleDiscovered(mDevice);
    					gatt.close();
    					mDevice.getConnectionCallback().onError(mDevice, gattStatusToString(status));
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
    	for (BluetoothGattService service: services) {
    		String serviceUUID = getShortUUID(service.getUuid().toString());
            if (!BleDeviceMode.containsService(serviceUUID)) continue;
            mDevice.setMode(BleDeviceMode.fromUuid(serviceUUID));

    		Log.d(TAG, "Discovered service on device " + mDevice.getName() + ": " + serviceUUID);
            if (mDevice.getMode().equals(BleDeviceMode.ON_BOARDING)) {
                setupDeviceForOnBoardingConnectionMode(service, gatt);
                mDevice.setStatus(BleDeviceStatus.CONNECTED);
                mBleDeviceEventCallback.onDeviceDiscovered(mDevice);
            } else if (mDevice.getMode().equals(BleDeviceMode.DIRECT_CONNECTION)) {
                setupDeviceForDirectConnectionMode(service, gatt);
    		} else if (mDevice.getMode().equals(BleDeviceMode.CONNECTED_TO_MASTER_MODULE)) {
                mDevice.setStatus(BleDeviceStatus.DISCONNECTED);
                mDevice.disconnect();
                mBleDeviceEventCallback.onDeviceConnectedToMasterModuleDiscovered(mDevice);
            }
            return;
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
    	if (status == BluetoothGatt.GATT_SUCCESS) {
            mDevice.getConnectionCallback().onWriteSuccess(mDevice, BleDeviceCharacteristic.from(characteristic));
        } else {
    	    mDevice.getConnectionCallback().onWriteError(mDevice, BleDeviceCharacteristic.from(characteristic), status);
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
        mDevice.setStatus(BleDeviceStatus.CONNECTED);
    	mDevice.setBluetoothGattService(service);
    	Log.d(TAG, "New mode on device " + mDevice.getName() + ": Direct connection");
    	List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
    	for (BluetoothGattCharacteristic characteristic:characteristics) {
    		String characteristicUUID = getShortUUID(characteristic.getUuid().toString());
    		if (characteristicUUID.equals(BleShortUUID.CHARACTERISTIC_DATA_READ)) {
    			gatt.setCharacteristicNotification(characteristic, true);
    			BluetoothGattDescriptor descriptor = characteristic.getDescriptor(RELAYR_NOTIFICATION_CHARACTERISTIC);
    			descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
    			gatt.writeDescriptor(descriptor);
    		}
    	}
    }

    private void setupDeviceForOnBoardingConnectionMode(BluetoothGattService service, BluetoothGatt gatt) {
        mDevice.setBluetoothGattService(service);
    	Log.d(TAG, "Device new mode on device " + mDevice.getName() + ": on boarding");
    }
}
