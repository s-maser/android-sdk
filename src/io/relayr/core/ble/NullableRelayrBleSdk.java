package io.relayr.core.ble;

import java.util.List;

import io.relayr.core.observers.Observable;
import io.relayr.core.observers.Observer;

class NullableRelayrBleSdk extends RelayrBleSdk {

    public void scan() { }

    public void stop() { }

    public boolean isScanning() { return false; }

    public void refresh() { }

    @Override
    public void subscribeToAllDevices(Observer<List<BleDevice>> observer) { }

    @Override
    public void subscribeToOnBoardingDevices(Observer<List<BleDevice>> observer) { }

    @Override
    public void subscribeToDirectConnectedDevices(Observer<List<BleDevice>> observer) { }

}
