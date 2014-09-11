package io.relayr.ble;

import java.util.Collection;
import java.util.List;

import io.relayr.SocketClient;
import io.relayr.model.TransmitterDevice;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;

class NullableRelayrBleSdk extends RelayrBleSdk {

    public Observable<List<BleDevice>> scan(Collection<BleDeviceType> deviceTypes) {
        return Observable.create(new Observable.OnSubscribe<List<BleDevice>>() {
            @Override
            public void call(Subscriber<? super List<BleDevice>> subscriber) {

            }
        });
    }

    public void stop() { }

    public boolean isScanning() { return false; }

    @Override
    public SocketClient getBleSocketClient() {
        return new SocketClient() {
            @Override
            public Subscription subscribe(TransmitterDevice device, Subscriber<Object> subscriber) {
                return new Subscription() {
                    @Override
                    public void unsubscribe() {

                    }

                    @Override
                    public boolean isUnsubscribed() {
                        return false;
                    }
                };
            }

            @Override
            public void unSubscribe(String sensorId) {

            }
        };
    }

}
