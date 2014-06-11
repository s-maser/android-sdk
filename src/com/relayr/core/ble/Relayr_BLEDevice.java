package com.relayr.core.ble;

import android.bluetooth.BluetoothDevice;

public class Relayr_BLEDevice {

	BluetoothDevice device;
	int rssi;

	public Relayr_BLEDevice(BluetoothDevice device, int rssi) {
		this.device = device;
		this.rssi = rssi;
	}

	public BluetoothDevice getDevice() {
		return device;
	}
	public void setDevice(BluetoothDevice device) {
		this.device = device;
	}
	public int getRssi() {
		return rssi;
	}
	public void setRssi(int rssi) {
		this.rssi = rssi;
	}
}
