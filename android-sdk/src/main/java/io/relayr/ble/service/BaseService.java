package io.relayr.ble.service;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothGatt;
import android.os.Build;

import io.relayr.ble.BleDevice;
import rx.Observable;

import static io.relayr.ble.service.ShortUUID.CHARACTERISTIC_BATTERY_LEVEL;
import static io.relayr.ble.service.ShortUUID.CHARACTERISTIC_FIRMWARE_VERSION;
import static io.relayr.ble.service.ShortUUID.CHARACTERISTIC_HARDWARE_VERSION;
import static io.relayr.ble.service.ShortUUID.CHARACTERISTIC_MANUFACTURER;
import static io.relayr.ble.service.ShortUUID.SERVICE_BATTERY_LEVEL;
import static io.relayr.ble.service.ShortUUID.SERVICE_DEVICE_INFO;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BaseService extends Service {

    protected final BleDevice mBleDevice;

    protected BaseService(BleDevice device, BluetoothGatt gatt, BluetoothGattReceiver receiver) {
        super(gatt, receiver);
        mBleDevice = device;
    }

    public Observable<? extends BluetoothGatt> disconnect() {
        return mBluetoothGattReceiver.disconnect(mBluetoothGatt);
    }

    /**
     * Return an observable of the Battery Level characteristic.
     * <p>See {@link BluetoothGatt#readCharacteristic} for details of what it's done internally.
     * @return an observable of the Battery Level characteristic
     */
    public Observable<Integer> getBatteryLevel() {
        return readByteAsAnIntegerCharacteristic(SERVICE_BATTERY_LEVEL,
                CHARACTERISTIC_BATTERY_LEVEL, "Battery Level");
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
