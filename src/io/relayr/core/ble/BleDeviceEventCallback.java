package io.relayr.core.ble;

interface BleDeviceEventCallback {
    void onModeSwitch(BleDeviceMode mode, BleDevice device);
    void onDeviceDiscovered(BleDevice device);
    void onUnknownDeviceDiscovered(BleDevice device);
}