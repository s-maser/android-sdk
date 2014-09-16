package io.relayr.ble;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class BleDeviceManager {

    private final Map<String, BleDevice> discoveredDevices = new HashMap<>();

    void addDiscoveredDevice(BleDevice device) {
        discoveredDevices.put(device.getAddress(), device);
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

    void refreshConnectedDevices() {
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
