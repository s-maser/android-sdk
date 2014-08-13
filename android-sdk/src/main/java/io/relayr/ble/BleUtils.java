package io.relayr.ble;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import io.relayr.RelayrApp;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public abstract class BleUtils {

    private static final int STATUS_BLE_ENABLED = 0;
    private static final int STATUS_BLUETOOTH_NOT_AVAILABLE = 1;
    private static final int STATUS_BLE_NOT_AVAILABLE = 2;
    private static final int STATUS_BLUETOOTH_DISABLED = 3;

    public static BluetoothAdapter getBluetoothAdapter(Context context) {
        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManager == null)
            return null;
        return bluetoothManager.getAdapter();
    }

    public static boolean isBleAvailable() {
        return BleUtils.getBleStatus(RelayrApp.get()) == STATUS_BLE_ENABLED;
    }

    private static boolean isSdk18() {
        return android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2;
    }

    public static boolean isBleSupported() {
        return isSdk18() && isBleSupported(RelayrApp.get());
    }

    private static boolean isBleSupported(Context context) {
        PackageManager packageManager = context.getPackageManager();
        return packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }

    private final static int REQUEST_ENABLE_BLUETOOTH = 1;

    public static void promptUserToActivateBluetooth(Activity activity) {
        BluetoothAdapter bluetoothAdapter = BleUtils.getBluetoothAdapter(RelayrApp.get());
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BLUETOOTH);
        }
    }

    private static int getBleStatus(Context context) {
        if (!isBleSupported()) return STATUS_BLE_NOT_AVAILABLE;

        BluetoothAdapter adapter = getBluetoothAdapter(context);
        return adapter == null ? STATUS_BLUETOOTH_NOT_AVAILABLE:
                adapter.isEnabled() ? STATUS_BLE_ENABLED:
                        STATUS_BLUETOOTH_DISABLED;
    }

    static String getShortUUID(String longUUID) {
        return longUUID.substring(4, 8);
    }
}
