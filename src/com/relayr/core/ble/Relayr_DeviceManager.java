package com.relayr.core.ble;

import android.annotation.TargetApi;
import android.os.Build;

import com.relayr.core.ble.device.Relayr_BLEDevice;
import com.relayr.core.ble.device.Relayr_BLEDeviceMode;
import com.relayr.core.ble.device.Relayr_BLEDeviceStatus;
import com.relayr.core.observers.Observable;
import com.relayr.core.observers.Observer;
import com.relayr.core.observers.Subscription;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class Relayr_DeviceManager {

	private HashMap<String,Relayr_BLEDevice> discoveredDevices;
	private Observable<List<Relayr_BLEDevice>> allDevicesObservable;
	private Observable<List<Relayr_BLEDevice>> onBoardingDevicesObservable;
	private Observable<List<Relayr_BLEDevice>> directConnectedDevicesObservable;

	protected Relayr_DeviceManager() {
		this.discoveredDevices = new HashMap<>();
		this.allDevicesObservable = new Observable<>();
		this.onBoardingDevicesObservable = new Observable<>();
		this.directConnectedDevicesObservable = new Observable<>();
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
			if (device != null) device.disconnect();
		}
		if (!discoveredDevices.isEmpty()) {
			discoveredDevices.clear();
		}
	}

	private List<Relayr_BLEDevice> getOnBoardingDevices() {
		List<Relayr_BLEDevice> list = new ArrayList<>();

		for (Relayr_BLEDevice device:discoveredDevices.values()) {
			if ((device != null) && (device.getMode() == Relayr_BLEDeviceMode.ONBOARDING)) {
				list.add(device);
			}
		}

		return list;
	}

	private List<Relayr_BLEDevice> getDirectConnectedDevices() {
		List<Relayr_BLEDevice> list = new ArrayList<>();

		for (Relayr_BLEDevice device:discoveredDevices.values()) {
			if ((device != null) &&(device.getMode() == Relayr_BLEDeviceMode.DIRECTCONNECTION)) {
				list.add(device);
			}
		}

		return list;
	}

	private List<Relayr_BLEDevice> getAllConfiguredDevices() {
		List<Relayr_BLEDevice> list = new ArrayList<>();

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
					device.refreshDeviceCache(device.gatt);
                    try {
                        device.gatt.discoverServices();
                    } catch (Exception e) { //DeadObjectException
                        device.disconnect();
                        device.setStatus(Relayr_BLEDeviceStatus.CONFIGURING);
                        device.connect();
                    }
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

	public Subscription<List<Relayr_BLEDevice>> subscribeToAllDevicesList(Observer<List<Relayr_BLEDevice>> observer) {
		allDevicesObservable.addObserver(observer);
		return new Subscription<>(observer, allDevicesObservable);
	}

	public Subscription<List<Relayr_BLEDevice>> subscribeToOnBoardingDevicesList(Observer<List<Relayr_BLEDevice>> observer) {
		onBoardingDevicesObservable.addObserver(observer);
		return new Subscription<>(observer, onBoardingDevicesObservable);
	}

	public Subscription<List<Relayr_BLEDevice>> subscribeToDirectConnectedDevicesList(Observer<List<Relayr_BLEDevice>> observer) {
		directConnectedDevicesObservable.addObserver(observer);
		return new Subscription<>(observer, directConnectedDevicesObservable);
	}


	public void removeDevice(Relayr_BLEDevice device) {
		discoveredDevices.remove(device.getAddress());
	}

}
