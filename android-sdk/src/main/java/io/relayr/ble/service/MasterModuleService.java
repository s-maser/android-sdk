package io.relayr.ble.service;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;

import rx.Observable;
import rx.functions.Func1;

public class MasterModuleService extends BaseService {
    protected MasterModuleService(BluetoothDevice device, BluetoothGatt gatt, BluetoothGattReceiver receiver) {
        super(device, gatt, receiver);
    }

    public static Observable<MasterModuleService> connect(final BluetoothDevice bluetoothDevice) {
        final BluetoothGattReceiver receiver = new BluetoothGattReceiver();
        return doConnect(bluetoothDevice, new BluetoothGattReceiver())
                .flatMap(new Func1<BluetoothGatt, Observable<MasterModuleService>>() {
                    @Override
                    public Observable<MasterModuleService> call(BluetoothGatt gatt) {
                        return Observable.just(new MasterModuleService(bluetoothDevice, gatt, receiver));
                    }
                });
    }

}
