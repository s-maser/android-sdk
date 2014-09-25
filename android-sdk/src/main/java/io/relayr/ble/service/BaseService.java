package io.relayr.ble.service;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.os.Build;

import rx.Observable;
import rx.functions.Func1;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BaseService {

    private final BluetoothDevice mBluetoothDevice;
    private final BluetoothGatt mBluetoothGatt;
    private final BluetoothGattReceiver mBluetoothGattReceiver;

    protected BaseService(BluetoothDevice device, BluetoothGatt gatt, BluetoothGattReceiver receiver) {
        mBluetoothDevice = device;
        mBluetoothGatt = gatt;
        mBluetoothGattReceiver = receiver;
    }

    /* package for testing */
    static Observable<? extends BaseService> connect(final BluetoothDevice bluetoothDevice,
                                                     final BluetoothGattReceiver receiver) {
        return receiver
                .connect(bluetoothDevice)
                .flatMap(new Func1<BluetoothGatt, Observable<BluetoothGatt>>() {
                    @Override
                    public Observable<BluetoothGatt> call(BluetoothGatt gatt) {
                        return receiver.discoverDevices(gatt);
                    }
                })
                .flatMap(new Func1<BluetoothGatt, Observable<BaseService>>() {
                    @Override
                    public Observable<BaseService> call(BluetoothGatt gatt) {
                        return Observable.just(new BaseService(bluetoothDevice, gatt, receiver));
                    }
                });
    }

    public static Observable<? extends BaseService> connect(final BluetoothDevice bluetoothDevice) {
        return connect(bluetoothDevice, new BluetoothGattReceiver());
    }

    public Observable<? extends BluetoothGatt> disconnect() {
        return mBluetoothGattReceiver.disconnect(mBluetoothGatt);
    }

    // device info 180F
    public void queryBatteryLevel() {} // 2A19

    // device info 180A
    public void queryFirmwareVersion() {} // 2A26
    public void queryHardwareVersion() {} // 2A27
    public void queryManufacturer() {} // 2A29

}
