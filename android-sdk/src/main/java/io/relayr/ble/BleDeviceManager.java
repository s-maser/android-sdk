package io.relayr.ble;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Subscriber;

class BleDeviceManager {

    private final Map<String, BleDevice> discoveredDevices = new HashMap<>();
    private Subscriber<? super List<BleDevice>> mDevicesSubscriber;

    void init(Subscriber<? super List<BleDevice>> devicesSubscriber) {
        mDevicesSubscriber = devicesSubscriber;
        refreshConnectedDevices();
    }

    void addDiscoveredDevice(BleDevice device) {
        discoveredDevices.put(device.getAddress(), device);
        if (mDevicesSubscriber != null) mDevicesSubscriber.onNext(getDiscoveredDevices());
    }

    boolean isDeviceDiscovered(String address) {
        return discoveredDevices.containsKey(address);
    }

    boolean isDeviceDiscovered(BleDevice device) {
        return isDeviceDiscovered(device.getAddress());
    }

    void clear() {
        for (BleDevice device: discoveredDevices.values()) {
            if (device.isConnected()) device.disconnect();
        }
        discoveredDevices.clear();
    }

    List<BleDevice> getDiscoveredDevices() {
        return new ArrayList<>(discoveredDevices.values());
    }

    private void refreshConnectedDevices() {
        for (BleDevice device: discoveredDevices.values()) {
            if (device.isConnected()) {
                device.forceCacheRefresh();
            }
        }
    }

    void removeDevice(BleDevice device) {
        discoveredDevices.remove(device.getAddress());
    }

}
