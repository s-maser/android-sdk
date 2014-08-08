package io.relayr.core.ble;

public enum BleDeviceMode {
    ON_BOARDING,
    DIRECT_CONNECTION,
    CONNECTED_TO_MASTER_MODULE;

    private static final String SERVICE_CONNECTED_TO_MASTER_MODULE = "2000";
    private static final String SERVICE_ON_BOARDING = "2001";
    private static final String SERVICE_DIRECT_CONNECTION = "2002";

    public static BleDeviceMode fromUuid(String serviceUuid) {
        return serviceUuid.equals(SERVICE_DIRECT_CONNECTION) ? DIRECT_CONNECTION:
                serviceUuid.equals(SERVICE_ON_BOARDING) ? ON_BOARDING:
                        CONNECTED_TO_MASTER_MODULE;
    }

    public static boolean containsService(String serviceUuid) {
        return serviceUuid.equals(SERVICE_DIRECT_CONNECTION) ||
                serviceUuid.equals(SERVICE_ON_BOARDING) ||
                serviceUuid.equals(SERVICE_CONNECTED_TO_MASTER_MODULE);
    }
}
