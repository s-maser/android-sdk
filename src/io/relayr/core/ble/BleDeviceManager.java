package io.relayr.core.ble;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.relayr.core.observers.Observable;
import io.relayr.core.observers.Observer;
import io.relayr.core.observers.Subscription;

public class BleDeviceManager {

    private HashMap<String, BleDevice> discoveredDevices;
    private Observable<List<BleDevice>> allDevicesObservable;
    private Observable<List<BleDevice>> onBoardingDevicesObservable;
    private Observable<List<BleDevice>> directConnectedDevicesObservable;

    protected BleDeviceManager() {
        this.discoveredDevices = new HashMap<>();
        this.allDevicesObservable = new Observable<>();
        this.onBoardingDevicesObservable = new Observable<>();
        this.directConnectedDevicesObservable = new Observable<>();
    }

    void notifyDiscoveredDevice(BleDevice device) {
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

    void addNewDevice(String address, BleDevice device) {
        discoveredDevices.put(address, device);
    }

    boolean isDeviceDiscovered(String address) {
        return this.discoveredDevices.containsKey(address);
    }

    boolean isFullyConfigured(String address) {
        return this.discoveredDevices.get(address) != null;
    }

    BleDevice getDevice(String address) {
        if (isDeviceDiscovered(address)) {
            return discoveredDevices.get(address);
        } else {
            return null;
        }
    }

    void clearDiscoveredDevices() {
        for (BleDevice device:discoveredDevices.values()) {
            if (device != null) device.disconnect();
        }
        discoveredDevices.clear();
    }

    private List<BleDevice> getOnBoardingDevices() {
        return getDevices(BleDeviceMode.ONBOARDING);
    }

    private List<BleDevice> getDirectConnectedDevices() {
        return getDevices(BleDeviceMode.DIRECTCONNECTION);
    }

    private List<BleDevice> getAllConfiguredDevices() {
        List<BleDevice> list = new ArrayList<>();

        for (BleDevice device:discoveredDevices.values()) {
            if ((device != null) && (device.getMode() != BleDeviceMode.UNKNOWN)) {
                list.add(device);
            }
        }

        return list;
    }

    private List<BleDevice> getDevices(BleDeviceMode deviceMode) {
        List<BleDevice> list = new ArrayList<>();

        for (BleDevice device: discoveredDevices.values()) {
            if ((device != null) && (device.getMode() == deviceMode)) {
                list.add(device);
            }
        }

        return list;
    }

    void refreshDiscoveredDevices() {
        for (BleDevice device: discoveredDevices.values()) {
            if (device != null) {
                if (device.isConnected()) {
                    device.forceCacheRefresh();
                } else {
                    device.setStatus(BleDeviceStatus.CONFIGURING);
                    device.connect();
                }
            }
        }
    }

    void onBoardingDeviceListUpdate() {
        onBoardingDevicesObservable.notifyObservers(getOnBoardingDevices());
    }

    void directConnectedDeviceListUpdate() {
        directConnectedDevicesObservable.notifyObservers(getDirectConnectedDevices());
    }

    public Subscription<List<BleDevice>> subscribeToAllDevicesList(Observer<List<BleDevice>> observer) {
        allDevicesObservable.addObserver(observer);
        return new Subscription<>(observer, allDevicesObservable);
    }

    public Subscription<List<BleDevice>> subscribeToOnBoardingDevicesList(Observer<List<BleDevice>> observer) {
        onBoardingDevicesObservable.addObserver(observer);
        return new Subscription<>(observer, onBoardingDevicesObservable);
    }

    public Subscription<List<BleDevice>> subscribeToDirectConnectedDevicesList(Observer<List<BleDevice>> observer) {
        directConnectedDevicesObservable.addObserver(observer);
        return new Subscription<>(observer, directConnectedDevicesObservable);
    }

    void removeDevice(BleDevice device) {
        discoveredDevices.remove(device.getAddress());
    }

}
