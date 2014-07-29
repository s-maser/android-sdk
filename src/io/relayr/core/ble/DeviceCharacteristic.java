package io.relayr.core.ble;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Build;

public enum DeviceCharacteristic {
	CONFIGURATION,
	SENSOR_ID,
	PASS_KEY,
	ON_BOARDING_FLAG,
	UNKNOWN;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static DeviceCharacteristic from(BluetoothGattCharacteristic characteristic) {
        return from(BleUtils.getShortUUID(characteristic.getUuid().toString()));
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
