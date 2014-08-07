package io.relayr.core.ble;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Build;

public enum BleDeviceCharacteristic {
	CONFIGURATION,
	SENSOR_ID,
	PASS_KEY,
	ON_BOARDING_FLAG,
	UNKNOWN;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static BleDeviceCharacteristic from(BluetoothGattCharacteristic characteristic) {
        return from(BleUtils.getShortUUID(characteristic.getUuid().toString()));
    }

    public static BleDeviceCharacteristic from(String uuid) {
        if (uuid.equals(BleShortUUID.CHARACTERISTIC_CONFIGURATION)) {
            return CONFIGURATION;
        }
        if (uuid.equals(BleShortUUID.CHARACTERISTIC_SENSOR_ID)) {
            return SENSOR_ID;
        }
        if (uuid.equals(BleShortUUID.CHARACTERISTIC_PASS_KEY)) {
            return PASS_KEY;
        }
        if (uuid.equals(BleShortUUID.CHARACTERISTIC_ON_BOARDING_FLAG)) {
            return ON_BOARDING_FLAG;
        }
        return UNKNOWN;
    }

}
