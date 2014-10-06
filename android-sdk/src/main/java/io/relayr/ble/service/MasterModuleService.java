package io.relayr.ble.service;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;

import io.relayr.ble.BleDevice;
import rx.Observable;
import rx.functions.Func1;

import static rx.Observable.just;

public class MasterModuleService extends BaseService {
    protected MasterModuleService(BleDevice bleDevice, BluetoothDevice device, BluetoothGatt gatt,
                                  BluetoothGattReceiver receiver) {
        super(bleDevice, device, gatt, receiver);
    }

    public static Observable<MasterModuleService> connect(final BleDevice bleDevice,
                                                          final BluetoothDevice device) {
        final BluetoothGattReceiver receiver = new BluetoothGattReceiver();
        return doConnect(device, receiver)
                .flatMap(new Func1<BluetoothGatt, Observable<MasterModuleService>>() {
                    @Override
                    public Observable<MasterModuleService> call(BluetoothGatt gatt) {
                        return just(new MasterModuleService(bleDevice, device, gatt, receiver));
                    }
                });
    }

}
