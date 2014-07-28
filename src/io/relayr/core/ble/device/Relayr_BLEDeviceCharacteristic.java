package io.relayr.core.ble.device;

public enum Relayr_BLEDeviceCharacteristic {
	CONFIGURATION,
	SENSOR_ID,
	PASS_KEY,
	ON_BOARDING_FLAG,
	UNKNOWN;

    public static Relayr_BLEDeviceCharacteristic from(String uuid) {
        if (uuid.equals(ShortUUID.CHARACTERISTIC_CONFIGURATION)) {
            return Relayr_BLEDeviceCharacteristic.CONFIGURATION;
        }
        if (uuid.equals(ShortUUID.CHARACTERISTIC_SENSOR_ID)) {
            return Relayr_BLEDeviceCharacteristic.SENSOR_ID;
        }
        if (uuid.equals(ShortUUID.CHARACTERISTIC_PASS_KEY)) {
            return Relayr_BLEDeviceCharacteristic.PASS_KEY;
        }
        if (uuid.equals(ShortUUID.CHARACTERISTIC_ON_BOARDING_FLAG)) {
            return Relayr_BLEDeviceCharacteristic.ON_BOARDING_FLAG;
        }
        return Relayr_BLEDeviceCharacteristic.UNKNOWN;
    }

}
