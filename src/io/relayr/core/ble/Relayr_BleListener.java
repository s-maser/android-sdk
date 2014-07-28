package io.relayr.core.ble;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import io.relayr.Relayr_Application;
import io.relayr.Relayr_Commons;
import io.relayr.core.ble.device.Relayr_BLEDevice;
import io.relayr.core.ble.device.Relayr_BLEDeviceStatus;
import io.relayr.core.ble.device.Relayr_BLEDeviceType;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class Relayr_BleListener {

    private static final String TAG = Relayr_BleListener.class.toString();

    private static Relayr_BleDevicesScanner scanner;
    public static Relayr_DeviceManager discoveredDevices;

    private final static int REQUEST_ENABLE_BT = 1;

    static {
        scanner = null;
        discoveredDevices = new Relayr_DeviceManager();
    }

    public static boolean init() {
        Log.d(TAG, "BleListener init called");
        if (!Relayr_Commons.isSDK18()) return false;
        Log.d(TAG, "BleListener init starting");
        final int bleStatus = Relayr_BleUtils.getBleStatus(Relayr_Application.currentActivity());
        switch (bleStatus) {
            case Relayr_BleUtils.STATUS_BLE_NOT_AVAILABLE:
            case Relayr_BleUtils.STATUS_BLUETOOTH_NOT_AVAILABLE:
                Log.d(TAG, "Bluethooth not available");
                return false;
            default:
                Activity currentActivity = Relayr_Application.currentActivity();
                BluetoothAdapter bluetoothAdapter = Relayr_BleUtils.getBluetoothAdapter(currentActivity);
                if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    currentActivity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                    return false;
                }
                scanner = new Relayr_BleDevicesScanner(bluetoothAdapter, new BluetoothAdapter.LeScanCallback() {

                    private boolean hasANewDeviceBeenDiscovered(BluetoothDevice device) {
                        return !discoveredDevices.isDeviceDiscovered(device.getAddress()) &&
                                Relayr_BLEDeviceType.getDeviceType(device.getName()) != Relayr_BLEDeviceType.Unknown;
                    }

                    @Override
                    public void onLeScan(BluetoothDevice device, final int rssi, byte[] scanRecord) {
                        if (hasANewDeviceBeenDiscovered(device)) {
                            Log.d(TAG, "New device: "+ device.getName() + " [" + device.getAddress() + "]");
                            Relayr_BLEDevice relayrDevice = new Relayr_BLEDevice(device);
                            discoveredDevices.addNewDevice(relayrDevice.getAddress(), null);
                            relayrDevice.setStatus(Relayr_BLEDeviceStatus.CONFIGURING);
                            Log.d(TAG, "Device cofiguration start: "+ relayrDevice.toString());
                            relayrDevice.connect();
                        }
                    }
                });
                scanner.setScanPeriod(7000);
                return true;
        }
    }

    public static void start() {
        if (!Relayr_Commons.isSDK18()) return;
        if (scanner != null) {
            if (!scanner.isScanning()) {
                discoveredDevices.clearDiscoveredDevices();
                startScanner();
            }
        } else {
            startScanner();
        }
    }

    private static void startScanner() {
        if (!init()) return;
        scanner.start();
        Log.d(TAG, "New scanner start: " + scanner.toString());
    }

    public static void refresh() {
        if (!Relayr_Commons.isSDK18()) return;
        if (scanner != null) {
            discoveredDevices.refreshDiscoveredDevices();
            if (!scanner.isScanning()) {
                startScanner();
            }
        } else {
            startScanner();
        }
    }

    public static void stop() {
        if (!Relayr_Commons.isSDK18() || scanner == null) return;
        scanner.stop();
        Log.d(TAG, "Scanner stop: " + scanner.toString());
    }

    public static boolean isScanning() {
        return Relayr_Commons.isSDK18() && scanner != null && scanner.isScanning();
    }

    public static Relayr_DeviceManager getDeviceManager() {
        return discoveredDevices;
    }
}
