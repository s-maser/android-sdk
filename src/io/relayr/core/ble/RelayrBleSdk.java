package io.relayr.core.ble;

import java.util.List;

import io.relayr.Relayr_SDK;
import io.relayr.core.observers.Observer;
import io.relayr.core.observers.Subscription;

public abstract class RelayrBleSdk {

    /** Provides the relayr sdk for bluetooth low energy or an empty implementation if bluetooth is
     * not available on the device. Before calling this method check
     * {@link io.relayr.Relayr_SDK#isBleSupported} and {@link io.relayr.Relayr_SDK#isBleAvailable}*/
    public static RelayrBleSdk newInstance() {
        return Relayr_SDK.isBleSupported() && Relayr_SDK.isBleAvailable() ?
                new RelayrBleSdkImpl():
                new NullableRelayrBleSdk();
    }

    /** Starts a scan for Bluetooth LE devices. */
    public abstract void scan();

    /** Stops an ongoing Bluetooth LE device scan. */
    public abstract void stop();

    /** Whether it's scanning for Bluetooth LE devices. */
    public abstract boolean isScanning();

    /** Refreshes the cache of the discovered Bluetooth LE devices and performs the {@link #scan()}
     * operation again. */
    public abstract void refresh();

    public abstract Subscription<List<BleDevice>> subscribeToAllDevices(Observer<List<BleDevice>> observer);

    public abstract Subscription<List<BleDevice>> subscribeToOnBoardingDevices(Observer<List<BleDevice>> observer);

    public abstract Subscription<List<BleDevice>> subscribeToDirectConnectedDevices(Observer<List<BleDevice>> observer);

}
