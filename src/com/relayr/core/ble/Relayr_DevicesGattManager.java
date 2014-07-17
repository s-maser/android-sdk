package com.relayr.core.ble;

import java.util.HashMap;

import com.relayr.core.ble.device.Relayr_BLEDevice;

import android.bluetooth.BluetoothGatt;

public class Relayr_DevicesGattManager {
	public static HashMap<String, BluetoothGatt> devicesGatt;

	static {
		devicesGatt = new HashMap<String, BluetoothGatt>();
	}


	public static void removeDevice(Relayr_BLEDevice device) {
		devicesGatt.remove(device.getAddress());
	}
}
