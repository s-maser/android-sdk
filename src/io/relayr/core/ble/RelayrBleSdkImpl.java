package io.relayr.core.ble;

class RelayrBleSdkImpl extends RelayrBleSdk {

    private final BleDevicesScannerManager mBleDevicesScannerManager;

    public RelayrBleSdkImpl() {
        mBleDevicesScannerManager = new BleDevicesScannerManager();
    }

    public void startScanning() {
        mBleDevicesScannerManager.start();
    }

    public void stopScanning() {
        mBleDevicesScannerManager.stop();
    }

    public boolean isScanning() {
        return mBleDevicesScannerManager.isScanning();
    }

    public void refreshScanning() {
        mBleDevicesScannerManager.refresh();
    }

    public BleDeviceManager getDeviceManager() {
        return mBleDevicesScannerManager.getDeviceManager();
    }
}
