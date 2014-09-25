package io.relayr.ble;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Build;

import io.relayr.ble.service.ShortUUID;

public enum BleDeviceCharacteristic {
	SENSOR_ID,
	PASS_KEY,
	ON_BOARDING_FLAG,
	UNKNOWN;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static BleDeviceCharacteristic from(BluetoothGattCharacteristic characteristic) {
        return from(BleUtils.getShortUUID(characteristic.getUuid()));
    }

    public static BleDeviceCharacteristic from(String uuid) {
        if (uuid.equals(ShortUUID.CHARACTERISTIC_SENSOR_ID)) {
            return SENSOR_ID;
        }
        if (uuid.equals(ShortUUID.CHARACTERISTIC_PASS_KEY)) {
            return PASS_KEY;
        }
        if (uuid.equals(ShortUUID.CHARACTERISTIC_ON_BOARDING_FLAG)) {
            return ON_BOARDING_FLAG;
        }
        return UNKNOWN;
    }

}
