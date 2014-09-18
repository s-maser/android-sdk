package io.relayr.ble;

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
import static io.relayr.ble.BleUtils.getShortUUID;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
class BleDeviceGattManager extends BluetoothGattCallback {

    private static final String TAG = BleDeviceGattManager.class.toString();

	private final BleDevice mDevice;
	private static final UUID RELAYR_NOTIFICATION_CHARACTERISTIC =
            UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    BleDeviceGattManager(BleDevice device) {
		mDevice = device;
	}

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
    	Log.d(TAG, "onConnectionStateChange");
        if (status != BluetoothGatt.GATT_SUCCESS) return;
    	if (newState == STATE_CONNECTED) {
    		Log.d(TAG, "Device " + mDevice.getName() + " connected");
            if (mDevice.getStatus() != BleDeviceStatus.CONNECTED) {
                Log.d(TAG, "Device " + mDevice.getName() + ": discoverServices");
                gatt.discoverServices();
            }
    	} else if (newState == STATE_DISCONNECTED) {
            if (mDevice.getStatus() != BleDeviceStatus.CONFIGURING) {
                mDevice.onDisconnect();
            } else {
                mDevice.setStatus(BleDeviceStatus.DISCONNECTED);
                Log.d(TAG, "Device " + mDevice.getName() + " configured");
                //mBleDeviceEventCallback.onConnectedDeviceDiscovered(mDevice);
            }
            Log.d(TAG, "Device disconnected");
        } else {
            if (BluetoothGattStatus.isFailureStatus(status)) {
                if (mDevice.getMode() == BleDeviceMode.CONNECTED_TO_MASTER_MODULE) {
                    Log.d(TAG, "Device " + mDevice.getName() + ": unhandled state change without configuration: " + BluetoothGattStatus.toString(status));
                    Log.d(TAG, "Device " + mDevice.getName() + ": removed because error in configuration process");
                    mDevice.disconnect();
                    mDevice.getConnectionCallback().onError(mDevice, BluetoothGattStatus.toString(status));
                } else {
                    Log.d(TAG, "Device " + mDevice.getName() + ": unhandled state change configured: " + BluetoothGattStatus.toString(status));
                }
            } else {
                Log.d(TAG, "Device " + mDevice.getName() + ": unhandled state change: " + BluetoothGattStatus.toString(status));
            }
    	}
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
    	List<BluetoothGattService> services = gatt.getServices();
    	for (BluetoothGattService service: services) {
    		String serviceUUID = getShortUUID(service.getUuid().toString());
            if (!BleDeviceMode.containsService(serviceUUID)) continue;
            mDevice.setStatus(BleDeviceStatus.CONNECTED);
            mDevice.setBluetoothGattService(service);
    		Log.d(TAG, "Discovered service on device " + mDevice.getName() + ": " + serviceUUID);
            if (mDevice.getMode().equals(BleDeviceMode.ON_BOARDING)) {

            } else if (mDevice.getMode().equals(BleDeviceMode.DIRECT_CONNECTION)) {
                setupDeviceForDirectConnectionMode(service, gatt);
    		} else if (mDevice.getMode().equals(BleDeviceMode.CONNECTED_TO_MASTER_MODULE)) {

            }
            mDevice.onConnect();
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
    	Log.d(TAG, "Characteristic wrote status on device " + mDevice.getName() + ": " + BluetoothGattStatus.toString(status));
    	if (status == BluetoothGatt.GATT_SUCCESS) {
            mDevice.getConnectionCallback().onWriteSuccess(mDevice, BleDeviceCharacteristic.from(characteristic));
        } else {
    	    mDevice.getConnectionCallback().onWriteError(mDevice, BleDeviceCharacteristic.from(characteristic), status);
    	}
    }

    private void setupDeviceForDirectConnectionMode(BluetoothGattService service, BluetoothGatt gatt) {
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

}
