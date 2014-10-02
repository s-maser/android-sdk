package io.relayr.ble.service;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Build;

import io.relayr.ble.service.error.CharacteristicNotFoundException;
import rx.Observable;
import rx.functions.Func1;

import static io.relayr.ble.service.ShortUUID.CHARACTERISTIC_BATTERY_LEVEL;
import static io.relayr.ble.service.ShortUUID.CHARACTERISTIC_FIRMWARE_VERSION;
import static io.relayr.ble.service.ShortUUID.CHARACTERISTIC_HARDWARE_VERSION;
import static io.relayr.ble.service.ShortUUID.CHARACTERISTIC_MANUFACTURER;
import static io.relayr.ble.service.ShortUUID.SERVICE_BATTERY_LEVEL;
import static io.relayr.ble.service.ShortUUID.SERVICE_DEVICE_INFO;
import static io.relayr.ble.service.Utils.getCharacteristicInServices;
import static rx.Observable.error;
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
        final String text = "Battery Level";
        return readCharacteristic(SERVICE_BATTERY_LEVEL, CHARACTERISTIC_BATTERY_LEVEL, text)
                .flatMap(new Func1<BluetoothGattCharacteristic, Observable<Integer>>() {
                    @Override
                    public Observable<Integer> call(BluetoothGattCharacteristic charac) {
                        if (charac.getValue() == null || charac.getValue().length == 0) {
                            error(new CharacteristicNotFoundException(text));
                        }
                        return just((int) charac.getValue()[0]);
                    }
                });
    }

    /**
     * Return an observable of the Firmware Version characteristic.
     * <p>See {@link BluetoothGatt#readCharacteristic} for details of what it's done internally.
     * @return an observable of the Firmware Version characteristic
     */
    public Observable<String> getFirmwareVersion() {
        String text = "Firmware Version";
        return readStringCharacteristic(SERVICE_DEVICE_INFO, CHARACTERISTIC_FIRMWARE_VERSION, text);
    }

    Observable<BluetoothGattCharacteristic> readCharacteristic(String serviceUuid,
                                                               String characteristicUuid,
                                                               final String what) {

        BluetoothGattCharacteristic characteristic = getCharacteristicInServices(
                mBluetoothGatt.getServices(), serviceUuid, characteristicUuid);
        if (characteristic == null) {
            return error(new CharacteristicNotFoundException(what));
        }
        return mBluetoothGattReceiver.readCharacteristic(mBluetoothGatt, characteristic);
    }


    private Observable<String> readStringCharacteristic(String serviceUuid,
                                                        String characteristicUuid,
                                                        final String what) {
        return readCharacteristic(serviceUuid, characteristicUuid, what)
                .flatMap(new Func1<BluetoothGattCharacteristic, Observable<String>>() {
                    @Override
                    public Observable<String> call(BluetoothGattCharacteristic charac) {
                        String value = charac.getStringValue(0);
                        if (value == null) {
                            return error(new CharacteristicNotFoundException(what));
                        }
                        return just(value);
                    }
                });
    }

    /**
     * Return an observable of the Hardware Version characteristic.
     * <p>See {@link BluetoothGatt#readCharacteristic} for details of what it's done internally.
     * @return an observable of the Hardware Version characteristic
     */
    public Observable<String> getHardwareVersion() {
        String text = "Hardware Version";
        return readStringCharacteristic(SERVICE_DEVICE_INFO, CHARACTERISTIC_HARDWARE_VERSION, text);
    }

    /**
     * Return an observable of the Manufacturer characteristic.
     * <p>See {@link BluetoothGatt#readCharacteristic} for details of what it's done internally.
     * @return an observable of the Manufacturer characteristic
     */
    public Observable<String> getManufacturer() {
        String text = "Manufacturer";
        return readStringCharacteristic(SERVICE_DEVICE_INFO, CHARACTERISTIC_MANUFACTURER, text);
    }

}
