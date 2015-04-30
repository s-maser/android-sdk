package io.relayr.ble;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import rx.Observable;
import rx.Subscriber;

class NullableRelayrBleSdk extends RelayrBleSdk {

    public Observable<List<BleDevice>> scan(Collection<BleDeviceType> deviceTypes) {
        return Observable.create(new Observable.OnSubscribe<List<BleDevice>>() {
            @Override
            public void call(Subscriber<? super List<BleDevice>> subscriber) {
                subscriber.onNext(Arrays.asList(new BleDevice(null, "Wunderbar MM",
                        BleDeviceMode.NEW_ON_BOARDING, new BleDeviceManager())));
            }
        });
    }

    @Override
    public BleDevice getPairedDevice(String macAddress) {
        return new BleDevice(null, "Wunderbar MM", BleDeviceMode.NEW_ON_BOARDING, new BleDeviceManager());
    }

    public void stop() {
    }

    public boolean isScanning() {
        return false;
    }

}
