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
     * Return the stored value of the Battery Level characteristic.
     * <p>See {@link BluetoothGattCharacteristic#getValue} for details.
     * @return Cached value of the characteristic
     */
    public int getBatteryLevel() {
        BluetoothGattCharacteristic characteristic = getCharacteristicInServices(
                mBluetoothGatt.getServices(), SERVICE_BATTERY_LEVEL, CHARACTERISTIC_BATTERY_LEVEL);
        if (characteristic == null || characteristic.getValue() == null) return -1;
        return characteristic.getValue().length == 0? -1: characteristic.getValue()[0];
    }

    /**
     * Return the stored value of the Firmware Version characteristic.
     * <p>See {@link BluetoothGattCharacteristic#getValue} for details.
     * @return Cached value of the characteristic
     */
    public String getFirmwareVersion() {
        return getCharacteristicInServicesAsString(
                mBluetoothGatt.getServices(), SERVICE_DEVICE_INFO, CHARACTERISTIC_FIRMWARE_VERSION);
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
