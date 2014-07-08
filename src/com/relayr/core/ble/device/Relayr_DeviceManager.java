package com.relayr.core.ble.device;

import java.util.Collection;
import java.util.HashMap;
import java.util.Observable;

import android.util.Log;

public class Relayr_DeviceManager extends Observable {

	private HashMap<String,Relayr_BLEDevice> discoveredDevices;

	public Relayr_DeviceManager() {
		this.discoveredDevices = new HashMap<String,Relayr_BLEDevice>();
	}

	public void addDiscoveredDevice(String address, Relayr_BLEDevice device) {
		Log.d(Relayr_DeviceManager.class.toString(), "Adding new device: " + device.getName());
		discoveredDevices.put(address, device);
		triggerObservers();
	}

	public boolean isDeviceDiscovered(String address) {
		return this.discoveredDevices.containsKey(address);
	}

	public Relayr_BLEDevice getDevice(String address) {
		if (isDeviceDiscovered(address)) {
			return discoveredDevices.get(address);
		} else {
			return null;
		}
	}

	public Collection<Relayr_BLEDevice> getDiscoveredDevices() {
		return discoveredDevices.values();
	}

	public void clearDiscoveredDevices() {
		discoveredDevices.clear();
		triggerObservers();
	}

	private void triggerObservers() {
		Log.d(Relayr_DeviceManager.class.toString(), "Observers triggered");
		setChanged();
		notifyObservers();
	}
}
