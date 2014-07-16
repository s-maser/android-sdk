package com.relayr.core.ble;

import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothGatt;
import android.os.Build;

import com.relayr.core.ble.device.Relayr_BLEDevice;
import com.relayr.core.ble.device.Relayr_BLEDeviceMode;
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

	protected void addDiscoveredDevice(String address, Relayr_BLEDevice device) {
		discoveredDevices.put(address, device);
		allDevicesObservable.notifyObservers(new ArrayList<Relayr_BLEDevice>(discoveredDevices.values()));
	}

	protected boolean isDeviceDiscovered(String address) {
		return this.discoveredDevices.containsKey(address);
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
			BluetoothGatt gatt = Relayr_DevicesGattManager.devicesGatt.get(device.getAddress());
			if (gatt != null) {
				gatt.close();
			}
			Relayr_DevicesGattManager.devicesGatt.remove(device.getAddress());
		}
		if (!discoveredDevices.isEmpty()) {
			discoveredDevices.clear();
		}
	}

	private ArrayList<Relayr_BLEDevice> getOnBoardingDevices() {
		ArrayList<Relayr_BLEDevice> list = new ArrayList<Relayr_BLEDevice>();

		for (Relayr_BLEDevice device:discoveredDevices.values()) {
			if (device.getMode() == Relayr_BLEDeviceMode.ONBOARDING) {
				list.add(device);
			}
		}

		return list;
	}

	private ArrayList<Relayr_BLEDevice> getDirectConnectedDevices() {
		ArrayList<Relayr_BLEDevice> list = new ArrayList<Relayr_BLEDevice>();

		for (Relayr_BLEDevice device:discoveredDevices.values()) {
			if (device.getMode() == Relayr_BLEDeviceMode.DIRECTCONNECTION) {
				list.add(device);
			}
		}

		return list;
	}

	protected void refreshDiscoveredDevices() {
		for (Relayr_BLEDevice device:discoveredDevices.values()) {
			device.connect();
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

}
