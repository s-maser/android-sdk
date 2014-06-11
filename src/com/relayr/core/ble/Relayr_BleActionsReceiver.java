package com.relayr.core.ble;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class Relayr_BleActionsReceiver extends BroadcastReceiver {

    private final Relayr_BleServiceListener bleServiceListener;

    public Relayr_BleActionsReceiver(Relayr_BleServiceListener listener) {
        bleServiceListener = listener;
    }

    /**
     * Handles various events fired by the Service.
     * ACTION_GATT_CONNECTED: connected to a GATT server.
     * ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
     * ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
     * ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
     *                        or notification operations.
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (Relayr_BleService.ACTION_GATT_CONNECTED.equals(action)) {
            bleServiceListener.onConnected();
        } else if (Relayr_BleService.ACTION_GATT_DISCONNECTED.equals(action)) {
            bleServiceListener.onDisconnected();
        } else if (Relayr_BleService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
            bleServiceListener.onServiceDiscovered();
        } else if (Relayr_BleService.ACTION_DATA_AVAILABLE.equals(action)) {
            final String serviceUuid = intent.getStringExtra(Relayr_BleService.EXTRA_SERVICE_UUID);
            final String characteristicUUid = intent.getStringExtra(Relayr_BleService.EXTRA_CHARACTERISTIC_UUID);
            final String text = intent.getStringExtra(Relayr_BleService.EXTRA_TEXT);
            final byte[] data = intent.getExtras().getByteArray(Relayr_BleService.EXTRA_DATA);
            bleServiceListener.onDataAvailable(serviceUuid, characteristicUUid, text, data);
        }
    }

    public static IntentFilter createIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Relayr_BleService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(Relayr_BleService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(Relayr_BleService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(Relayr_BleService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

}
