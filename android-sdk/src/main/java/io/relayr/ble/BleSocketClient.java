package io.relayr.ble;

import java.util.HashSet;
import java.util.List;

import io.relayr.RelayrSdk;
import io.relayr.SocketClient;
import io.relayr.model.TransmitterDevice;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

import static java.util.Arrays.asList;

public class BleSocketClient implements SocketClient {

    @Override
    public Subscription subscribe(TransmitterDevice device, final Subscriber<Object> subscriber) {
        return RelayrSdk.getRelayrBleSdk()
                .scan(new HashSet<>(asList(BleDeviceType.from(device.getModel()))))
                .flatMap(new Func1<List<BleDevice>, Observable<BleDevice>>() {
                    @Override
                    public Observable<BleDevice> call(final List<BleDevice> bleDevices) {
                        return Observable.create(new Observable.OnSubscribe<BleDevice>() {
                            @Override
                            public void call(Subscriber<? super BleDevice> subscriber) {
                                // TODO: read BleDevice sensorID characteristic,
                                // TODO: compare it with the TransmitterDevice and filter them out
                                for (BleDevice bleDevice : bleDevices)
                                    if (bleDevice.getMode().equals(BleDeviceMode.DIRECT_CONNECTION))
                                        subscriber.onNext(bleDevice);
                            }
                        });
                    }
                })
                .flatMap(new Func1<BleDevice, Observable<String>>() {
                    @Override
                    public Observable<String> call(BleDevice bleDevice) {
                        return bleDevice.subscribeToDeviceValueChanges();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        subscriber.onError(e);
                    }

                    @Override
                    public void onNext(String o) {
                        subscriber.onNext(o);
                    }
                });
    }

    @Override
    public void unSubscribe(String sensorId) {

    }

}
