package com.relayr.core.ble;

import java.util.HashMap;

import android.bluetooth.BluetoothGatt;

public class Relayr_DevicesGattManager {
	public static HashMap<String, BluetoothGatt> devicesGatt;

	static {
		devicesGatt = new HashMap<String, BluetoothGatt>();
	}
}
