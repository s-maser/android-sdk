package io.relayr.ble;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.relayr.RelayrSdk;
import io.relayr.SocketClient;
import io.relayr.model.DeviceModel;
import io.relayr.model.TransmitterDevice;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

public class BleSocketClient implements SocketClient {

    @Override
    public Subscription subscribe(TransmitterDevice device, final Subscriber<Object> subscriber) {
        Set<BleDeviceType> deviceTypeSet = new HashSet<>();
        deviceTypeSet.add(BleDeviceType.from(DeviceModel.from(device)));
        return RelayrSdk.getRelayrBleSdk()
                .scan(deviceTypeSet)
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
                .flatMap(new Func1<BleDevice, Observable<BleDeviceValue>>() {
                    @Override
                    public Observable<BleDeviceValue> call(BleDevice bleDevice) {
                        return bleDevice.subscribeToDeviceValueChanges();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<BleDeviceValue>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        subscriber.onError(e);
                    }

                    @Override
                    public void onNext(BleDeviceValue bleDeviceValue) {
                        subscriber.onNext(bleDeviceValue.getFormattedValue().toString());
                    }
                });
    }

    @Override
    public void unSubscribe(String sensorId) {

    }

}
