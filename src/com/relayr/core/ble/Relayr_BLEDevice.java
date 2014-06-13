package com.relayr.core.ble;

import android.bluetooth.BluetoothDevice;

public class Relayr_BLEDevice {

	BluetoothDevice device;
	int rssi;
	byte[] scanRecord;

	public Relayr_BLEDevice(BluetoothDevice device, int rssi, byte[] scanRecord) {
		this.device = device;
		this.rssi = rssi;
		this.scanRecord = scanRecord;
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

	public byte[] getScanRecord() {
		return scanRecord;
	}

	public void setScanRecord(byte[] scanRecord) {
		this.scanRecord = scanRecord;
	}

	public boolean connect() {

		return false;
	}

}
