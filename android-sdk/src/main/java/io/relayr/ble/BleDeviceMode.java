package io.relayr.ble;

import java.util.List;

import static io.relayr.ble.service.ShortUUID.SERVICE_CONNECTED_TO_MASTER_MODULE;
import static io.relayr.ble.service.ShortUUID.SERVICE_DIRECT_CONNECTION;
import static io.relayr.ble.service.ShortUUID.SERVICE_ON_BOARDING;
/**
 * A relayr Device may be in one of the following modes:
 * ON_BOARDING - In onboarding mode, when first being configured and registered on the relayr cloud
 * DIRECT_CONNECTION - Connected via BLE to a transmitter or an App.
 * CONNECTED_TO_MASTER_MODULE - Connected to the WunderBar Master Module
 * UNKNOWN
 * The Device would publish a different based on the mode it is in.
 */
public enum BleDeviceMode {
    ON_BOARDING,
    DIRECT_CONNECTION,
    CONNECTED_TO_MASTER_MODULE,
    UNKNOWN;

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
