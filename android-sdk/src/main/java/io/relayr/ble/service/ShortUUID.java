package io.relayr.ble.service;

public interface ShortUUID {

    // General services & characteristics
    static final String SERVICE_BATTERY_LEVEL = "180f";
    static final String CHARACTERISTIC_BATTERY_LEVEL = "2a19";

    static final String SERVICE_DEVICE_INFO = "180a";
    static final String CHARACTERISTIC_FIRMWARE_VERSION = "2a26";
    static final String CHARACTERISTIC_HARDWARE_VERSION = "2a27";
    static final String CHARACTERISTIC_MANUFACTURER = "2a29";

    // Relayr related services
    static final String SERVICE_CONNECTED_TO_MASTER_MODULE = "2000";

    static final String SERVICE_ON_BOARDING = "2001";
    static final String CHARACTERISTIC_SENSOR_ID = "2010";
    static final String CHARACTERISTIC_PASS_KEY = "2018";
    static final String CHARACTERISTIC_ON_BOARDING_FLAG = "2019";

    static final String SERVICE_DIRECT_CONNECTION = "2002";
    static final String CHARACTERISTIC_DATA_READ = "2016";
}
