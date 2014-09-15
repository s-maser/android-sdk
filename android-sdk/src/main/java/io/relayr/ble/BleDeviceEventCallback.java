package io.relayr.ble;

interface BleDeviceEventCallback {
    void onModeSwitch(BleDeviceMode mode, BleDevice device);
    void onConnectedDeviceDiscovered(BleDevice device);
}