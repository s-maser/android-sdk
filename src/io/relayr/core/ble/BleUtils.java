package io.relayr.core.ble;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import io.relayr.Relayr_Application;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public abstract class BleUtils {

    private static final int STATUS_BLE_ENABLED = 0;
    private static final int STATUS_BLUETOOTH_NOT_AVAILABLE = 1;
    private static final int STATUS_BLE_NOT_AVAILABLE = 2;
    private static final int STATUS_BLUETOOTH_DISABLED = 3;

    static BluetoothAdapter getBluetoothAdapter(Context context) {
        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManager == null)
            return null;
        return bluetoothManager.getAdapter();
    }

    public static boolean isBleAvailable() {
        final int bleStatus = BleUtils.getBleStatus(Relayr_Application.currentActivity());
        return bleStatus != STATUS_BLE_NOT_AVAILABLE && bleStatus != STATUS_BLUETOOTH_NOT_AVAILABLE;
    }

    private final static int REQUEST_ENABLE_BLUETOOTH = 1;

    public static void promptUserToActivateBluetooth() {
        Activity currentActivity = Relayr_Application.currentActivity();
        BluetoothAdapter bluetoothAdapter = BleUtils.getBluetoothAdapter(currentActivity);
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            currentActivity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BLUETOOTH);
        }
    }

    private static int getBleStatus(Context context) {
        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            return STATUS_BLE_NOT_AVAILABLE;
        }

        final BluetoothAdapter adapter = getBluetoothAdapter(context);
        // Checks if Bluetooth is supported on the device.
        if (adapter == null) {
            return STATUS_BLUETOOTH_NOT_AVAILABLE;
        }

        if (adapter.isEnabled())
            return STATUS_BLUETOOTH_DISABLED;

        return STATUS_BLE_ENABLED;
    }

    static String getShortUUID(String longUUID) {
        return longUUID.substring(4, 8);
    }
}
