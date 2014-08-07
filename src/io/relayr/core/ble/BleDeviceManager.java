package io.relayr.core.ble;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class BleDeviceManager {

    private HashMap<String, BleDevice> discoveredDevices;

    protected BleDeviceManager() {
        discoveredDevices = new HashMap<>();
    }

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

    List<BleDevice> getAllConfiguredDevices() {
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

    void removeDevice(BleDevice device) {
        discoveredDevices.remove(device.getAddress());
    }

}
