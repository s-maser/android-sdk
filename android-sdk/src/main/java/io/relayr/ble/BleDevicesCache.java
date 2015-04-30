package io.relayr.ble;

import android.bluetooth.BluetoothGattCharacteristic;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.relayr.RelayrSdk;
import io.relayr.ble.service.BaseService;
import io.relayr.ble.service.DirectConnectionService;
import io.relayr.model.TransmitterDevice;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;

import static io.relayr.ble.BleDeviceMode.DIRECT_CONNECTION;
import static java.util.Arrays.asList;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static rx.Observable.just;

@Singleton
public class BleDevicesCache {

    private final Map<TransmitterDevice, BaseService> mCache = new ConcurrentHashMap<>();

    @Inject public BleDevicesCache() {
    }

    public Observable<BaseService> getSensorForDevice(final TransmitterDevice device) {
        if (!mCache.containsKey(device)) {
            Set<BleDeviceType> deviceTypeSet = new HashSet<>(asList(BleDeviceType.from(device.getModel())));
            return RelayrSdk.getRelayrBleSdk()
                    .scan(deviceTypeSet)
                    .timeout(20, TimeUnit.SECONDS)
                    .flatMap(new Func1<List<BleDevice>, Observable<BleDevice>>() {
                        @Override
                        public Observable<BleDevice> call(final List<BleDevice> bleDevices) {
                            return Observable.create(new Observable.OnSubscribe<BleDevice>() {
                                @Override
                                public void call(Subscriber<? super BleDevice> subscriber) {
                                    for (BleDevice bleDevice : bleDevices)
                                        if (bleDevice.getMode().equals(DIRECT_CONNECTION))
                                            subscriber.onNext(bleDevice);
                                }
                            });
                        }
                    })
                    .filter(new Func1<BleDevice, Boolean>() {
                        @Override
                        public Boolean call(BleDevice bleDevice) {
                            return !mCache.containsKey(device);
                        }
                    })
                    .take(1)
                    .flatMap(new Func1<BleDevice, Observable<? extends BaseService>>() {
                        @Override
                        public Observable<? extends BaseService> call(BleDevice bleDevice) {
                            RelayrSdk.getRelayrBleSdk().stop();
                            return bleDevice.connect();
                        }
                    })
                    .flatMap(new Func1<BaseService, Observable<BaseService>>() {
                        @Override
                        public Observable<BaseService> call(final BaseService baseService) {
                            boolean deviceInterestedIn = baseService instanceof DirectConnectionService;
                            if (!deviceInterestedIn) {
                                return baseService
                                        .getBleDevice()
                                        .disconnect()
                                        .map(new Func1<BleDevice, BaseService>() {
                                            @Override
                                            public BaseService call(BleDevice bleDevice) {
                                                return null;
                                            }
                                        });
                            }
                            DirectConnectionService service = (DirectConnectionService) baseService;
                            return service.getSensorId()
                                    .flatMap(new Func1<UUID, Observable<BaseService>>() {
                                        @Override
                                        public Observable<BaseService> call(UUID uuid) {
                                            boolean deviceInterestedIn = device.id.equals(uuid.toString());
                                            if (deviceInterestedIn) {
                                                return just(baseService);
                                            } else {
                                                return baseService
                                                        .getBleDevice()
                                                        .disconnect()
                                                        .map(new Func1<BleDevice, BaseService>() {
                                                            @Override
                                                            public BaseService call(BleDevice bleDevice) {
                                                                return null;
                                                            }
                                                        });
                                            }

                                        }
                                    });
                        }
                    })
                    .filter(new Func1<BaseService, Boolean>() {
                        @Override
                        public Boolean call(BaseService baseService) {
                            return baseService != null;
                        }
                    })
                    .doOnNext(new Action1<BaseService>() {
                        @Override
                        public void call(BaseService service) {
                            mCache.put(device, service);
                        }
                    })
                    .doOnError(new Action1<Throwable>() {
                        @Override public void call(Throwable throwable) {
                            RelayrSdk.getRelayrBleSdk().stop();
                        }
                    })
                    .delay(300, MILLISECONDS);
        }

        return just(mCache.get(device));
    }

    /** Disconnects all the devices and clears the cache */
    public void clean() {
        for (final BaseService service : mCache.values()) {
            (service instanceof DirectConnectionService ?
                    (((DirectConnectionService) service)
                            .stopGettingReadings()
                            .map(new Func1<BluetoothGattCharacteristic, BaseService>() {
                                @Override
                                public BaseService call(BluetoothGattCharacteristic c) {
                                    return service;
                                }
                            })) :
                    just(service))
                    .flatMap(new Func1<BaseService, Observable<BleDevice>>() {
                        @Override
                        public Observable<BleDevice> call(BaseService baseService) {
                            return baseService.getBleDevice().disconnect();
                        }
                    })
                    .subscribe(new Observer<BleDevice>() {
                        @Override
                        public void onCompleted() {
                        }

                        @Override
                        public void onError(Throwable e) {
                        }

                        @Override
                        public void onNext(BleDevice bleDevice) {
                        }
                    });
        }
        mCache.clear();
    }

}
