package io.relayr.ble.service;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Build;

import io.relayr.ble.service.error.GattException;
import rx.Observable;
import rx.functions.Func1;

import static io.relayr.ble.service.ShortUUID.CHARACTERISTIC_BATTERY_LEVEL;
import static io.relayr.ble.service.ShortUUID.CHARACTERISTIC_FIRMWARE_VERSION;
import static io.relayr.ble.service.ShortUUID.CHARACTERISTIC_HARDWARE_VERSION;
import static io.relayr.ble.service.ShortUUID.CHARACTERISTIC_MANUFACTURER;
import static io.relayr.ble.service.ShortUUID.SERVICE_BATTERY_LEVEL;
import static io.relayr.ble.service.ShortUUID.SERVICE_DEVICE_INFO;
import static io.relayr.ble.service.Utils.getCharacteristicInServices;
import static io.relayr.ble.service.Utils.getCharacteristicInServicesAsString;
import static rx.Observable.*;
import static rx.Observable.just;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BaseService {

    protected final BluetoothDevice mBluetoothDevice;
    protected final BluetoothGatt mBluetoothGatt;
    protected final BluetoothGattReceiver mBluetoothGattReceiver;

    /* package for testing */
    BaseService(BluetoothDevice device, BluetoothGatt gatt, BluetoothGattReceiver receiver) {
        mBluetoothDevice = device;
        mBluetoothGatt = gatt;
        mBluetoothGattReceiver = receiver;
    }

    /* package for testing */
    static Observable<? extends BluetoothGatt> doConnect(final BluetoothDevice bluetoothDevice,
                                                         final BluetoothGattReceiver receiver) {
        return receiver
                .connect(bluetoothDevice)
                .flatMap(new Func1<BluetoothGatt, Observable<BluetoothGatt>>() {
                    @Override
                    public Observable<BluetoothGatt> call(BluetoothGatt gatt) {
                        return receiver.discoverDevices(gatt);
                    }
                });
    }

    public Observable<? extends BluetoothGatt> disconnect() {
        return mBluetoothGattReceiver.disconnect(mBluetoothGatt);
    }

    protected Observable<BluetoothGattCharacteristic> write(byte[] bytes,
                                                          String serviceUuid,
                                                          String characteristicUuid) {
        BluetoothGattCharacteristic characteristic = getCharacteristicInServices(
                mBluetoothGatt.getServices(), serviceUuid, characteristicUuid);
        characteristic.setValue(bytes);
        return mBluetoothGattReceiver.writeCharacteristic(mBluetoothGatt, characteristic);
    }

    /**
     * Return an observable of the Battery Level characteristic.
     * <p>See {@link BluetoothGatt#readCharacteristic} for details of what it's done internally.
     * @return an observable of the Battery Level characteristic
     */
    public Observable<Integer> getBatteryLevel() {
        BluetoothGattCharacteristic characteristic = getCharacteristicInServices(
                mBluetoothGatt.getServices(), SERVICE_BATTERY_LEVEL, CHARACTERISTIC_BATTERY_LEVEL);
        if (characteristic == null) {
            return error(new GattException("Battery Level Characteristic not found."));
        }
        return mBluetoothGattReceiver
                .readCharacteristic(mBluetoothGatt, characteristic)
                .flatMap(new Func1<BluetoothGattCharacteristic, Observable<Integer>>() {
                    @Override
                    public Observable<Integer> call(BluetoothGattCharacteristic charac) {
                        if (charac.getValue() == null || charac.getValue().length == 0) {
                            error(new GattException("Battery Level Characteristic not found."));
                        }
                        return just((int) charac.getValue()[0]);
                    }
                });
    }

    /**
     * Return the stored value of the Firmware Version characteristic.
     * <p>See {@link BluetoothGatt#readCharacteristic} for details of what it's done internally.
     * @return an observable of the Firmware Version characteristic
     */
    public Observable<String> getFirmwareVersion() {
        BluetoothGattCharacteristic characteristic = getCharacteristicInServices(
                mBluetoothGatt.getServices(), SERVICE_DEVICE_INFO, CHARACTERISTIC_FIRMWARE_VERSION);
        if (characteristic.getValue() == null) {
            return error(new GattException("Firmware Version Characteristic not found."));
        }
        return mBluetoothGattReceiver
                .readCharacteristic(mBluetoothGatt, characteristic)
                .flatMap(new Func1<BluetoothGattCharacteristic, Observable<String>>() {
                    @Override
                    public Observable<String> call(BluetoothGattCharacteristic charac) {
                        if (charac.getValue() == null || charac.getValue().length == 0) {
                            error(new GattException("Firmware Version Characteristic not found."));
                        }
                        return just(charac.getStringValue(0));
                    }
                });
    }

    /**
     * Return the stored value of the Hardware Version characteristic.
     * <p>See {@link BluetoothGattCharacteristic#getValue} for details.
     * @return Cached value of the characteristic
     */
    public String getHardwareVersion() {
        return getCharacteristicInServicesAsString(
                mBluetoothGatt.getServices(), SERVICE_DEVICE_INFO, CHARACTERISTIC_HARDWARE_VERSION);
    }

    /**
     * Return the stored value of the Manufacturer characteristic.
     * <p>See {@link BluetoothGattCharacteristic#getValue} for details.
     * @return Cached value of the characteristic
     */
    public String getManufacturer() {
        return getCharacteristicInServicesAsString(
                mBluetoothGatt.getServices(), SERVICE_DEVICE_INFO, CHARACTERISTIC_MANUFACTURER);
    }

}
