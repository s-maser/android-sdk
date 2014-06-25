package com.relayr.core.ble;

import java.io.Serializable;

import com.relayr.Relayr_Application;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.os.Build;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class Relayr_BLEDevice implements Serializable, Comparable<Relayr_BLEDevice>{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private BluetoothGatt gatt;

	private String name;
	private String address;

	public Relayr_BLEDevice(String name, String address) {
		this.name = name;
		this.address = address;
		this.gatt = null;
	}

	public String getName() {
		return name;
	}

	public String getAddress() {
		return address;
	}

	public void connect() {
		BluetoothDevice device = Relayr_BleListener.discoveredDevices.get(address);
		gatt = device.connectGatt(Relayr_Application.currentActivity(), true, new Relayr_BleGattExecutor(this));
	}

	public void disconnect() {
		if (gatt != null) {
			gatt.disconnect();
			gatt = null;
		}
	}

	public boolean isConnected() {
		return gatt != null;
	}

	@Override
	public int compareTo(Relayr_BLEDevice another) {
		int nameComparison = this.name.compareTo(another.getName());
		if (nameComparison != 0){
			return 0;
		} else {
			int addressComparison = this.address.compareTo(another.getAddress());
			return addressComparison;
		}
	}

	public boolean equalTo(Relayr_BLEDevice another) {
		return this.compareTo(another) == 0;
	}

	@Override
	public String toString() {
		return getName() + " - [" + getAddress() + "]";
	}

}
