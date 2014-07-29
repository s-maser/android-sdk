package io.relayr.core.ble;

class NullableRelayrBleSdk extends RelayrBleSdk {

    public void startBLEScanning() { }

    public void stopBLEScanning() { }

    public boolean isScanningForBLE() { return false; }

    public void refreshBLEScanning() { }

    public BleDeviceManager getBLEDeviceManager() { return null; }

}
