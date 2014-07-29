package io.relayr.core.ble;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Build;
import android.util.Log;

import io.relayr.Relayr_Application;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
class BleDevicesScannerManager implements BleDeviceEventCallback {

    private static final String TAG = BleDevicesScannerManager.class.toString();
    private static final int SCAN_PERIOD_IN_MILLISECONDS = 7000;

    private final BleDevicesScanner scanner;
    private final BleDeviceManager discoveredDevices = new BleDeviceManager();

    BleDevicesScannerManager() {
        Log.d(TAG, "BleListener init starting");

        Activity currentActivity = Relayr_Application.currentActivity();
        BluetoothAdapter bluetoothAdapter = BleUtils.getBluetoothAdapter(currentActivity);
        scanner = new BleDevicesScanner(bluetoothAdapter, new BluetoothAdapter.LeScanCallback() {

            private boolean hasANewDeviceBeenDiscovered(BluetoothDevice device) {
                return !discoveredDevices.isDeviceDiscovered(device.getAddress()) &&
                        BleDeviceType.getDeviceType(device.getName()) != BleDeviceType.Unknown;
            }

            @Override
            public void onLeScan(BluetoothDevice device, final int rssi, byte[] scanRecord) {
                if (hasANewDeviceBeenDiscovered(device)) {
                    Log.d(TAG, "New device: "+ device.getName() + " [" + device.getAddress() + "]");
                    BleDevice relayrDevice = new BleDevice(device, BleDevicesScannerManager.this);
                    discoveredDevices.addNewDevice(relayrDevice.getAddress(), null);
                    relayrDevice.setStatus(BleDeviceStatus.CONFIGURING);
                    Log.d(TAG, "Device cofiguration start: "+ relayrDevice.toString());
                    relayrDevice.connect();
                }
            }
        });
        scanner.setScanPeriod(SCAN_PERIOD_IN_MILLISECONDS);
    }

    public void start() {
        if (!scanner.isScanning()) {
            discoveredDevices.clearDiscoveredDevices();
            scanner.start();
            Log.d(TAG, "New scanner start");
        }
    }

    public void refresh() {
        discoveredDevices.refreshDiscoveredDevices();
        if (!scanner.isScanning()) {
            scanner.start();
            Log.d(TAG, "New scanner start");
        }
    }

    public void stop() {
        scanner.stop();
        Log.d(TAG, "Scanner stop");
    }

    public boolean isScanning() {
        return scanner.isScanning();
    }

    public BleDeviceManager getDeviceManager() {
        return discoveredDevices;
    }

    @Override
    public void onModeSwitch(BleDeviceMode mode, BleDevice device) {
        if (discoveredDevices.isFullyConfigured(device.getAddress())) {
            switch (mode) {
                case ONBOARDING: {
                    discoveredDevices.onBoardingDeviceListUpdate();
                    break;
                }
                case DIRECTCONNECTION: {
                    discoveredDevices.directConnectedDeviceListUpdate();
                    break;
                }
                default:break;
            }
        }
    }

    @Override
    public void onDeviceDiscovered(BleDevice device) {
        if (!discoveredDevices.isFullyConfigured(device.getAddress())) {
            discoveredDevices.addNewDevice(device.getAddress(), device);
            Log.d(TAG, "Device " + device.getName() + " added to discovered devices");
            discoveredDevices.notifyDiscoveredDevice(device);
        }
    }

    @Override
    public void onUnknownDeviceDiscovered(BleDevice device) {
        discoveredDevices.removeDevice(device);
    }
}
