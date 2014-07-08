package com.relayr.core.ble;

import java.util.ArrayList;
import java.util.UUID;

import com.relayr.core.ble.device.Relayr_BLEDevice;
import com.relayr.core.ble.device.Relayr_BLEDeviceStatus;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.os.Build;
import android.util.Log;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class Relayr_BleGattCallback extends BluetoothGattCallback {

	Relayr_BLEDevice device;
	private UUID RELAYR_NOTIFICATION_CHARACTERISTIC = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

	public Relayr_BleGattCallback(Relayr_BLEDevice device) {
		super();
		this.device = device;
	}

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
    	if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_CONNECTED) {
    		Log.d(Relayr_BleGattCallback.class.toString(), "Device connected");
    		this.device.setStatus(Relayr_BLEDeviceStatus.CONNECTED);
    		this.device.triggerObservers();
    		gatt.discoverServices();
    	} else {
    		if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_DISCONNECTED) {
    			Log.d(Relayr_BleGattCallback.class.toString(), "Device disconnected");
    			this.device.setStatus(Relayr_BLEDeviceStatus.DISCONNECTED);
    			this.device.triggerObservers();
    		}
    	}
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
    	ArrayList<BluetoothGattService> services = (ArrayList<BluetoothGattService>) gatt.getServices();
    	for (BluetoothGattService service:services) {
    		String serviceUUID = service.getUuid().toString();
    		Log.d(Relayr_BleGattCallback.class.toString(), "Discovered service: " + serviceUUID);
    		if (serviceUUID.equals(this.device.getType().serviceUUID)) {
    			ArrayList<BluetoothGattCharacteristic> characteristics = (ArrayList<BluetoothGattCharacteristic>) service.getCharacteristics();
    			for (BluetoothGattCharacteristic characteristic:characteristics) {
    				String characteristicUUID = characteristic.getUuid().toString();
    				Log.d(Relayr_BleGattCallback.class.toString(), "Discovered characteristic: " + characteristicUUID);
    				Log.d(Relayr_BleGattCallback.class.toString(), "Check if is data read characteristic: " + this.device.getType().dataReadCharacteristicUUID);
    				if (characteristicUUID.equals(this.device.getType().dataReadCharacteristicUUID)) {
    					Log.d(Relayr_BleGattCallback.class.toString(), "Discovered data read characteristic: " + characteristicUUID);
    					gatt.setCharacteristicNotification(characteristic, true);
    					BluetoothGattDescriptor descriptor = characteristic.getDescriptor(RELAYR_NOTIFICATION_CHARACTERISTIC);
    				    descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
    				    gatt.writeDescriptor(descriptor);
    				} else {
    					Log.d(Relayr_BleGattCallback.class.toString(), "Check if is configuration characteristic: " + this.device.getType().configurationCharacteristicUUID);
    					if (characteristicUUID.equals(this.device.getType().configurationCharacteristicUUID)) {
    						Log.d(Relayr_BleGattCallback.class.toString(), "Discovered configuration characteristic: " + characteristicUUID);
    						this.device.setRelayrConfigurationCharacteristic(characteristic);
    					}
    				}
    			}
    			break;
    		}
    	}
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
    	//Log.d(Relayr_BleGattCallback.class.toString(), "Characteristic readed: " + characteristic.getUuid());
    	//Log.d(Relayr_BleGattCallback.class.toString(), "Characteristic readed value: " + characteristic.getValue());
        this.device.setValue(characteristic.getValue());
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
    	Log.d(Relayr_BleGattCallback.class.toString(), "Characteristic wrote: " + characteristic.getUuid());
    	switch (status) {
    	case BluetoothGatt.GATT_FAILURE: {
    		Log.d(Relayr_BleGattCallback.class.toString(), "Status: GATT_FAILURE");
    		break;
    	}
    	case BluetoothGatt.GATT_INSUFFICIENT_AUTHENTICATION: {
    		Log.d(Relayr_BleGattCallback.class.toString(), "Status: GATT_INSUFFICIENT_AUTHENTICATION");
    		break;
    	}
    	case BluetoothGatt.GATT_INSUFFICIENT_ENCRYPTION: {
    		Log.d(Relayr_BleGattCallback.class.toString(), "Status: GATT_INSUFFICIENT_ENCRYPTION");
    		break;
    	}
    	case BluetoothGatt.GATT_INVALID_ATTRIBUTE_LENGTH: {
    		Log.d(Relayr_BleGattCallback.class.toString(), "Status: GATT_INVALID_ATTRIBUTE_LENGTH");
    		break;
    	}
    	case BluetoothGatt.GATT_INVALID_OFFSET: {
    		Log.d(Relayr_BleGattCallback.class.toString(), "Status: GATT_INVALID_OFFSET");
    		break;
    	}
    	case BluetoothGatt.GATT_READ_NOT_PERMITTED: {
    		Log.d(Relayr_BleGattCallback.class.toString(), "Status: GATT_READ_NOT_PERMITTED");
    		break;
    	}
    	case BluetoothGatt.GATT_REQUEST_NOT_SUPPORTED: {
    		Log.d(Relayr_BleGattCallback.class.toString(), "Status: GATT_REQUEST_NOT_SUPPORTED");
    		break;
    	}
    	case BluetoothGatt.GATT_SUCCESS: {
    		Log.d(Relayr_BleGattCallback.class.toString(), "Status: GATT_SUCCESS");
    		break;
    	}
    	case BluetoothGatt.GATT_WRITE_NOT_PERMITTED: {
    		Log.d(Relayr_BleGattCallback.class.toString(), "Status: GATT_WRITE_NOT_PERMITTED");
    		break;
    	}
    	}
    }
}
