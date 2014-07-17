package com.relayr.core.ble;

import java.util.ArrayList;
import java.util.UUID;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.os.Build;
import android.util.Log;

import com.relayr.core.ble.device.Relayr_BLEDevice;
import com.relayr.core.ble.device.Relayr_BLEDeviceMode;
import com.relayr.core.ble.device.Relayr_BLEDeviceStatus;

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
    	Log.d(Relayr_BleGattCallback.class.toString(), "onConnectionStateChange");
    	if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_CONNECTED) {
    		Log.d(Relayr_BleGattCallback.class.toString(), "Device connected");
    		this.device.setStatus(Relayr_BLEDeviceStatus.CONNECTED);
    		gatt.discoverServices();
    		if (this.device.connectionCallback != null) {
    			Log.d(Relayr_BleGattCallback.class.toString(), "Callback detected: sending onConnect event");
    			this.device.connectionCallback.onConnect(this.device);
    		} else {
    			Log.d(Relayr_BleGattCallback.class.toString(), "Callback not detected: not sending onConnect event");
    		}
    	} else {
    		if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_DISCONNECTED) {
    			Log.d(Relayr_BleGattCallback.class.toString(), "Device disconnected");
    			this.device.setStatus(Relayr_BLEDeviceStatus.DISCONNECTED);
    			this.device.setMode(Relayr_BLEDeviceMode.UNKNOWN);
    			if (this.device.connectionCallback != null) {
    				Log.d(Relayr_BleGattCallback.class.toString(), "Callback detected: sending onDisconnect event");
        			this.device.connectionCallback.onDisconnect(this.device);
        		} else {
        			Log.d(Relayr_BleGattCallback.class.toString(), "Callback not detected: not sending onDisconnect event");
        		}
    			if (this.device.getStatus() != Relayr_BLEDeviceStatus.DISCONNECTING) {
    				Log.d(Relayr_BleGattCallback.class.toString(), "Not disconnected by the user --> Automatic reconnecting");
    				this.device.connect();
    			} else {
    				Log.d(Relayr_BleGattCallback.class.toString(), "Disconnected by the user --> No automatic reconnecting");
    			}
    		} else {
    			Log.d(Relayr_BleGattCallback.class.toString(), "Device unhandled state change: " + gattStatusToString(status));
    			if (this.device.connectionCallback != null) {
        			this.device.connectionCallback.onError(this.device, gattStatusToString(status));
        		}
    		}
    	}
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
    	ArrayList<BluetoothGattService> services = (ArrayList<BluetoothGattService>) gatt.getServices();
    	for (BluetoothGattService service:services) {
    		String serviceUUID = getShortUUID(service.getUuid().toString());
    		Log.d(Relayr_BleGattCallback.class.toString(), "Discovered service: " + serviceUUID);
    		if (serviceUUID.equals(this.device.getType().directConnectionUUID)) {
    			setupDeviceForDirectConnectionMode(service, gatt);
    			break;
    		}
    		if (serviceUUID.equals(this.device.getType().onBoardingUUID)) {
    			setupDeviceForOnBoardingConnectionMode(service, gatt);
    			break;
    		}
    	}
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        this.device.setValue(characteristic.getValue());
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
    	Log.d(Relayr_BleGattCallback.class.toString(), "Characteristic wrote: " + characteristic.getUuid());
    	Log.d(Relayr_BleGattCallback.class.toString(), "Characteristic wrote status: " + gattStatusToString(status));
    }

    private String getShortUUID(String longUUID) {
    	return longUUID.substring(4, 8);
    }

    private String gattStatusToString(int status) {
    	switch (status) {
    	case BluetoothGatt.GATT_FAILURE: {
    		return "Not identified error";
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

    private void setupDeviceForDirectConnectionMode(BluetoothGattService service, BluetoothGatt gatt) {
    	this.device.setMode(Relayr_BLEDeviceMode.DIRECTCONNECTION);
    	this.device.currentService = service;
    	Log.d(Relayr_BleGattCallback.class.toString(), "Device new mode: Direct connection");
    	ArrayList<BluetoothGattCharacteristic> characteristics = (ArrayList<BluetoothGattCharacteristic>) service.getCharacteristics();
    	for (BluetoothGattCharacteristic characteristic:characteristics) {
    		String characteristicUUID = getShortUUID(characteristic.getUuid().toString());
    		if (characteristicUUID.equals(this.device.getType().dataReadCharacteristicUUID)) {
    			gatt.setCharacteristicNotification(characteristic, true);
    			BluetoothGattDescriptor descriptor = characteristic.getDescriptor(RELAYR_NOTIFICATION_CHARACTERISTIC);
    			descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
    			gatt.writeDescriptor(descriptor);
    		}
    	}
    }

    private void setupDeviceForOnBoardingConnectionMode(BluetoothGattService service, BluetoothGatt gatt) {
    	this.device.setMode(Relayr_BLEDeviceMode.ONBOARDING);
    	this.device.currentService = service;
    	Log.d(Relayr_BleGattCallback.class.toString(), "Device new mode: on boarding");
    }
}
