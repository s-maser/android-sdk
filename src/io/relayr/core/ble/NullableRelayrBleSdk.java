package io.relayr.core.ble;

class NullableRelayrBleSdk extends RelayrBleSdk {

    public void startScanning() { }

    public void stopScanning() { }

    public boolean isScanning() { return false; }

    public void refreshScanning() { }

    public BleDeviceManager getDeviceManager() { return null; }

}
