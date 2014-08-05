package io.relayr.core.ble;

import java.util.List;

import io.relayr.core.observers.Observable;
import io.relayr.core.observers.Observer;
import io.relayr.core.observers.Subscription;

class NullableRelayrBleSdk extends RelayrBleSdk {

    public void scan() { }

    public void stop() { }

    public boolean isScanning() { return false; }

    public void refresh() { }

    @Override
    public Subscription<List<BleDevice>> subscribeToAllDevices(Observer<List<BleDevice>> observer) {
        return new Subscription<>(observer, new Observable<List<BleDevice>>());
    }

    @Override
    public Subscription<List<BleDevice>> subscribeToOnBoardingDevices(Observer<List<BleDevice>> observer) {
        return new Subscription<>(observer, new Observable<List<BleDevice>>());
    }

    @Override
    public Subscription<List<BleDevice>> subscribeToDirectConnectedDevices(Observer<List<BleDevice>> observer) {
        return new Subscription<>(observer, new Observable<List<BleDevice>>());
    }

}
