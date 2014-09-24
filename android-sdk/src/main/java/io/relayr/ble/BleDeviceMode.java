package io.relayr.ble;

import java.util.List;

public enum BleDeviceMode {
    ON_BOARDING,
    DIRECT_CONNECTION,
    CONNECTED_TO_MASTER_MODULE,
    UNKNOWN;

    private static final String SERVICE_CONNECTED_TO_MASTER_MODULE = "2000";
    private static final String SERVICE_ON_BOARDING = "2001";
    private static final String SERVICE_DIRECT_CONNECTION = "2002";

    public static BleDeviceMode fromUuid(String serviceUuid) {
        return serviceUuid.equals(SERVICE_DIRECT_CONNECTION) ? DIRECT_CONNECTION:
                serviceUuid.equals(SERVICE_ON_BOARDING) ? ON_BOARDING:
                serviceUuid.equals(SERVICE_CONNECTED_TO_MASTER_MODULE) ? CONNECTED_TO_MASTER_MODULE:
                        UNKNOWN;
    }

    public static boolean containsService(String serviceUuid) {
        return !fromUuid(serviceUuid).equals(UNKNOWN);
    }

    public static BleDeviceMode fromServiceUuids(List<String> uuids) {
        if (uuids == null || uuids.isEmpty()) return UNKNOWN;
        BleDeviceMode mode;
        for (String uuid: uuids) {
            mode = BleDeviceMode.fromUuid(uuid);
            if (!mode.equals(UNKNOWN)) return mode;
        }
        return UNKNOWN;
    }

    @Override
    public String toString() {
        switch(this) {
            case ON_BOARDING: {
                return "MODE_ON_BOARDING";
            }
            case DIRECT_CONNECTION: {
                return "MODE_DIRECT_CONNECTION";
            }
            default: {
                return "CONNECTED_TO_MASTER_MODULE";
            }
        }
    }
}
