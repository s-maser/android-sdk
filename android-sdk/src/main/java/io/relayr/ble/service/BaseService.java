package io.relayr.ble.service;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Build;

import rx.Observable;
import rx.functions.Func1;

import static io.relayr.ble.service.ShortUUID.*;
import static io.relayr.ble.service.Utils.getCharacteristicInServices;
import static io.relayr.ble.service.Utils.getCharacteristicInServicesAsString;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BaseService {

    private final BluetoothDevice mBluetoothDevice;
    private final BluetoothGatt mBluetoothGatt;
    private final BluetoothGattReceiver mBluetoothGattReceiver;

    /* package for testing */
    BaseService(BluetoothDevice device, BluetoothGatt gatt, BluetoothGattReceiver receiver) {
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

    /**
     * Return the stored value of the Battery Level characteristic.
     * <p>See {@link BluetoothGattCharacteristic#getValue} for details.
     * @return Cached value of the characteristic
     */
    public int getBatteryLevel() {
        BluetoothGattCharacteristic characteristic = getCharacteristicInServices(
                mBluetoothGatt.getServices(), BATTERY_LEVEL_SERVICE, BATTERY_LEVEL_CHARACTERISTIC);
        if (characteristic == null) return -1;
        return characteristic.getValue().length == 0? -1: characteristic.getValue()[0];
    }

    /**
     * Return the stored value of the Firmware Version characteristic.
     * <p>See {@link BluetoothGattCharacteristic#getValue} for details.
     * @return Cached value of the characteristic
     */
    public String getFirmwareVersion() {
        return getCharacteristicInServicesAsString(
                mBluetoothGatt.getServices(), DEVICE_INFO_SERVICE, FIRMWARE_VERSION_CHARACTERISTIC);
    }

    /**
     * Return the stored value of the Hardware Version characteristic.
     * <p>See {@link BluetoothGattCharacteristic#getValue} for details.
     * @return Cached value of the characteristic
     */
    public String getHardwareVersion() {
        return getCharacteristicInServicesAsString(
                mBluetoothGatt.getServices(), DEVICE_INFO_SERVICE, HARDWARE_VERSION_CHARACTERISTIC);
    }

    /**
     * Return the stored value of the Manufacturer characteristic.
     * <p>See {@link BluetoothGattCharacteristic#getValue} for details.
     * @return Cached value of the characteristic
     */
    public String getManufacturer() {
        return getCharacteristicInServicesAsString(
                mBluetoothGatt.getServices(), DEVICE_INFO_SERVICE, MANUFACTURER_CHARACTERISTIC);
    }

}
