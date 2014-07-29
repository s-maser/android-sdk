package io.relayr.core.ble.device;

import android.bluetooth.BluetoothGattCharacteristic;

import io.relayr.core.ble.Relayr_BleUtils;

public enum DeviceCharacteristic {
	CONFIGURATION,
	SENSOR_ID,
	PASS_KEY,
	ON_BOARDING_FLAG,
	UNKNOWN;

    public static DeviceCharacteristic from(BluetoothGattCharacteristic characteristic) {
        return from(Relayr_BleUtils.getShortUUID(characteristic.getUuid().toString()));
    }
    public static DeviceCharacteristic from(String uuid) {
        if (uuid.equals(ShortUUID.CHARACTERISTIC_CONFIGURATION)) {
            return DeviceCharacteristic.CONFIGURATION;
        }
        if (uuid.equals(ShortUUID.CHARACTERISTIC_SENSOR_ID)) {
            return DeviceCharacteristic.SENSOR_ID;
        }
        if (uuid.equals(ShortUUID.CHARACTERISTIC_PASS_KEY)) {
            return DeviceCharacteristic.PASS_KEY;
        }
        if (uuid.equals(ShortUUID.CHARACTERISTIC_ON_BOARDING_FLAG)) {
            return DeviceCharacteristic.ON_BOARDING_FLAG;
        }
        return DeviceCharacteristic.UNKNOWN;
    }

}
