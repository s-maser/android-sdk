package io.relayr.core.ble;

import io.relayr.Relayr_SDK;

public abstract class RelayrBleSdk {

    /** Provides the relayr sdk for bluetooth low energy or an empty implementation if bluetooth is
     * not available on the device. Before calling this method check
     * {@link io.relayr.Relayr_SDK#isBleSupported} and {@link io.relayr.Relayr_SDK#isBleAvailable}*/
    public static RelayrBleSdk newInstance() {
        return Relayr_SDK.isBleSupported() && Relayr_SDK.isBleAvailable() ?
                new RelayrBleSdkImpl():
                new NullableRelayrBleSdk();
    }

    public abstract void startScanning();

    public abstract void stopScanning();

    public abstract boolean isScanning();

    public abstract void refreshScanning();

    public abstract BleDeviceManager getDeviceManager();

}
