package io.relayr.ble;

import java.util.Collection;
import java.util.List;

import io.relayr.RelayrSdk;
import rx.Observable;

/**
 * This class handles all methods related to BLE (Bluetooth Low Energy) communication.
 */
public abstract class RelayrBleSdk {

    /**
     * Provides the relayr sdk with a BLE implementation or an empty implementation,
     * in case bluetooth is not available on the device.
     * An empty implementation is one in which the three methods below do not function
     * This call should be preceded by {@link io.relayr.RelayrSdk#isBleSupported}
     * to check whether BLE is supported
     * and by {@link io.relayr.RelayrSdk#isBleAvailable} to check whether BLE is activated
     */
    public static RelayrBleSdk newInstance() {
        return RelayrSdk.isBleSupported() && RelayrSdk.isBleAvailable() ?
                new RelayrBleSdkImpl():
                new NullableRelayrBleSdk();
    }

    /**
     * Starts a scan for BLE devices.
     * Since the sensor mode may change, the cache of all found devices is refreshed
     * and they will be discovered again upon a following scan.
     * @param deviceTypes a collection containing all ble type devices you are interested in
     */
    public abstract Observable<List<BleDevice>> scan(Collection<BleDeviceType> deviceTypes);

    /**
     * Stops an ongoing BLE device scan.
     */
    public abstract void stop();

    /**
     * Checks whether a scan for BLE devices is taking place.
     * Returns true in case it is, false otherwise.
     */
    public abstract boolean isScanning();

}
