package com.relayr.core.ble;

import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.TargetApi;
import android.os.Build;

import com.relayr.core.ble.device.Relayr_BLEDevice;
import com.relayr.core.ble.device.Relayr_BLEDeviceMode;
import com.relayr.core.ble.device.Relayr_BLEDeviceStatus;
import com.relayr.core.observers.Observable;
import com.relayr.core.observers.Observer;
import com.relayr.core.observers.Subscription;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class Relayr_DeviceManager {

	private HashMap<String,Relayr_BLEDevice> discoveredDevices;
	private Observable<ArrayList<Relayr_BLEDevice>> allDevicesObservable;
	private Observable<ArrayList<Relayr_BLEDevice>> onBoardingDevicesObservable;
	private Observable<ArrayList<Relayr_BLEDevice>> directConnectedDevicesObservable;

	protected Relayr_DeviceManager() {
		this.discoveredDevices = new HashMap<String,Relayr_BLEDevice>();
		this.allDevicesObservable = new Observable<ArrayList<Relayr_BLEDevice>>();
		this.onBoardingDevicesObservable = new Observable<ArrayList<Relayr_BLEDevice>>();
		this.directConnectedDevicesObservable = new Observable<ArrayList<Relayr_BLEDevice>>();
	}

	protected void notifyDiscoveredDevice(Relayr_BLEDevice device) {
		allDevicesObservable.notifyObservers(getAllConfiguredDevices());
		switch (device.getMode()) {
		case ONBOARDING: {
			onBoardingDeviceListUpdate();
			break;
		}
		case DIRECTCONNECTION: {
			directConnectedDeviceListUpdate();
			break;
		}
		default:break;
		}
	}

	protected void addNewDevice(String address, Relayr_BLEDevice device) {
		discoveredDevices.put(address, device);
	}

	public boolean isDeviceDiscovered(String address) {
		return this.discoveredDevices.containsKey(address);
	}

	public boolean isFullyConfigured(String address) {
		return this.discoveredDevices.get(address) != null;
	}

	protected Relayr_BLEDevice getDevice(String address) {
		if (isDeviceDiscovered(address)) {
			return discoveredDevices.get(address);
		} else {
			return null;
		}
	}

	protected void clearDiscoveredDevices() {
		for (Relayr_BLEDevice device:discoveredDevices.values()) {
			device.disconnect();
		}
		if (!discoveredDevices.isEmpty()) {
			discoveredDevices.clear();
		}
	}

	private ArrayList<Relayr_BLEDevice> getOnBoardingDevices() {
		ArrayList<Relayr_BLEDevice> list = new ArrayList<Relayr_BLEDevice>();

		for (Relayr_BLEDevice device:discoveredDevices.values()) {
			if ((device != null) && (device.getMode() == Relayr_BLEDeviceMode.ONBOARDING)) {
				list.add(device);
			}
		}

		return list;
	}

	private ArrayList<Relayr_BLEDevice> getDirectConnectedDevices() {
		ArrayList<Relayr_BLEDevice> list = new ArrayList<Relayr_BLEDevice>();

		for (Relayr_BLEDevice device:discoveredDevices.values()) {
			if ((device != null) &&(device.getMode() == Relayr_BLEDeviceMode.DIRECTCONNECTION)) {
				list.add(device);
			}
		}

		return list;
	}

	private ArrayList<Relayr_BLEDevice> getAllConfiguredDevices() {
		ArrayList<Relayr_BLEDevice> list = new ArrayList<Relayr_BLEDevice>();

		for (Relayr_BLEDevice device:discoveredDevices.values()) {
			if ((device != null) && (device.getMode() != Relayr_BLEDeviceMode.UNKNOWN)) {
				list.add(device);
			}
		}

		return list;
	}

	protected void refreshDiscoveredDevices() {
		for (Relayr_BLEDevice device:discoveredDevices.values()) {
			if (device != null) {
				if (device.isConnected()) {
					device.gatt.discoverServices();
				} else {
					device.setStatus(Relayr_BLEDeviceStatus.CONFIGURING);
					device.connect();
				}
			}
		}
	}

	public void onBoardingDeviceListUpdate() {
		onBoardingDevicesObservable.notifyObservers(getOnBoardingDevices());
	}

	public void directConnectedDeviceListUpdate() {
		directConnectedDevicesObservable.notifyObservers(getDirectConnectedDevices());
	}

	public Subscription<ArrayList<Relayr_BLEDevice>> subscribeToAllDevicesList(Observer<ArrayList<Relayr_BLEDevice>> observer) {
		allDevicesObservable.addObserver(observer);
		return new Subscription<ArrayList<Relayr_BLEDevice>>(observer, allDevicesObservable);
	}

	public Subscription<ArrayList<Relayr_BLEDevice>> subscribeToOnBoardingDevicesList(Observer<ArrayList<Relayr_BLEDevice>> observer) {
		onBoardingDevicesObservable.addObserver(observer);
		return new Subscription<ArrayList<Relayr_BLEDevice>>(observer, onBoardingDevicesObservable);
	}

	public Subscription<ArrayList<Relayr_BLEDevice>> subscribeToDirectConnectedDevicesList(Observer<ArrayList<Relayr_BLEDevice>> observer) {
		directConnectedDevicesObservable.addObserver(observer);
		return new Subscription<ArrayList<Relayr_BLEDevice>>(observer, directConnectedDevicesObservable);
	}


	public void removeDevice(Relayr_BLEDevice device) {
		discoveredDevices.remove(device.getAddress());
	}

}
