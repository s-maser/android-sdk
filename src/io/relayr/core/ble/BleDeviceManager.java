package io.relayr.core.ble;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class BleDeviceManager {

    private final HashMap<String, BleDevice> discoveredDevices = new HashMap<>();

    void addNewDevice(String address, BleDevice device) {
        discoveredDevices.put(address, device);
    }

    boolean isDeviceDiscovered(String address) {
        return discoveredDevices.containsKey(address);
    }

    boolean isFullyConfigured(String address) {
        return discoveredDevices.get(address) != null;
    }

    void clearDiscoveredDevices() {
        for (BleDevice device: discoveredDevices.values()) {
            if (device != null) device.disconnect();
        }
        discoveredDevices.clear();
    }

    List<BleDevice> getAllConfiguredDevices() {
        List<BleDevice> configuredDevices = new ArrayList<>();

        for (BleDevice device: discoveredDevices.values()) {
            if ((device != null) && (device.getMode() != BleDeviceMode.CONNECTED_TO_MASTER_MODULE)) {
                configuredDevices.add(device);
            }
        }

        return configuredDevices;
    }

    void refreshDiscoveredDevices() {
        for (BleDevice device: discoveredDevices.values()) {
            if (device != null) {
                if (device.isConnected()) {
                    device.forceCacheRefresh();
                } else {
                    device.connect();
                }
            }
        }
    }

    void removeDevice(BleDevice device) {
        discoveredDevices.remove(device.getAddress());
    }

}
