package io.relayr.core.ble;

import java.util.List;

import io.relayr.Relayr_SDK;
import rx.Observable;

public abstract class RelayrBleSdk {

    /** Provides the relayr sdk for bluetooth low energy or an empty implementation if bluetooth is
     * not available on the device. Before calling this method check
     * {@link io.relayr.Relayr_SDK#isBleSupported} and {@link io.relayr.Relayr_SDK#isBleAvailable}*/
    public static RelayrBleSdk newInstance() {
        return Relayr_SDK.isBleSupported() && Relayr_SDK.isBleAvailable() ?
                new RelayrBleSdkImpl():
                new NullableRelayrBleSdk();
    }

    /** Starts a scan for Bluetooth LE devices. Since there can be changes in the mode of a sensor,
     * the cache of all found devices will be refreshed and they will be discovered again. */
    public abstract Observable<List<BleDevice>> scan();

    /** Stops an ongoing Bluetooth LE device scan. */
    public abstract void stop();

    /** Whether it's scanning for Bluetooth LE devices. */
    public abstract boolean isScanning();

}
