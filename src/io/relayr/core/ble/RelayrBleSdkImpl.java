package io.relayr.core.ble;

class RelayrBleSdkImpl extends RelayrBleSdk {

    private final BleDevicesScannerManager mBleDevicesScannerManager;

    public RelayrBleSdkImpl() {
        mBleDevicesScannerManager = new BleDevicesScannerManager();
    }

    public void startBLEScanning() {
        mBleDevicesScannerManager.start();
    }

    public void stopBLEScanning() {
        mBleDevicesScannerManager.stop();
    }

    public boolean isScanningForBLE() {
        return mBleDevicesScannerManager.isScanning();
    }

    public void refreshBLEScanning() {
        mBleDevicesScannerManager.refresh();
    }

    public BleDeviceManager getBLEDeviceManager() {
        return mBleDevicesScannerManager.getDeviceManager();
    }
}
