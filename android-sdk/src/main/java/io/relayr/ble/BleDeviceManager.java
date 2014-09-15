package io.relayr.ble;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

class BleDeviceManager {

    private final Set<String> discoveredDevices = new HashSet<>();
    private final Map<String, BleDevice> connectedDevices = new HashMap<>();

    // when connected to master module - disconnect - remove from connected devices & but save as discovered and don't try to reconfigure
    // also after connection, read the id and save it to the ble object
    // also find a way to keep all info from ble device although it's not connected

    void addNewDiscoveredDevice(BleDevice device) {
        discoveredDevices.add(device.getAddress());
    }

    public void addNewConnectedDevice(BleDevice device) {
        connectedDevices.put(device.getAddress(), device);
    }

    boolean isDeviceDiscovered(String address) {
        return discoveredDevices.contains(address);
    }

    boolean isDeviceDiscovered(BleDevice device) {
        return isDeviceDiscovered(device.getAddress());
    }

    boolean isDiscoveredDeviceConnected(BleDevice device) {
        return connectedDevices.containsKey(device.getAddress());
    }

    void clear() {
        for (BleDevice device: connectedDevices.values()) {
            device.disconnect();
        }
        connectedDevices.clear();
        discoveredDevices.clear();
    }

    List<BleDevice> getConnectedDevices() {
        List<BleDevice> configuredDevices = new ArrayList<>();

        for (BleDevice device: connectedDevices.values()) {
            configuredDevices.add(device);
        }

        return configuredDevices;
    }

    void refreshConnectedDevices() {
        for (BleDevice device: connectedDevices.values()) {
            if (device.isConnected()) {
                device.forceCacheRefresh();
            } else {
                device.connect();
            }
        }
    }

    void removeDevice(BleDevice device) {
        connectedDevices.remove(device.getAddress());
        discoveredDevices.remove(device.getAddress());
    }

}
