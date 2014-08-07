package io.relayr.core.ble;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Build;
import android.util.Log;

import java.util.List;

import io.relayr.Relayr_Application;
import rx.Observable;
import rx.Subscriber;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
class RelayrBleSdkImpl extends RelayrBleSdk implements BleDeviceEventCallback {

    private static final String TAG = RelayrBleSdkImpl.class.toString();
    private static final int SCAN_PERIOD_IN_MILLISECONDS = 7000;

    private final BleDevicesScanner scanner;
    private final BleDeviceManager discoveredDevices = new BleDeviceManager();
    private Subscriber<? super List<BleDevice>> mDevicesSubscriber;

    RelayrBleSdkImpl() {
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
                    BleDevice relayrDevice = new BleDevice(device, RelayrBleSdkImpl.this);
                    discoveredDevices.addNewDevice(relayrDevice.getAddress(), null);
                    relayrDevice.setStatus(BleDeviceStatus.CONFIGURING);
                    Log.d(TAG, "Device configuration start: "+ relayrDevice.toString());
                    relayrDevice.connect();
                }
            }
        });
        scanner.setScanPeriod(SCAN_PERIOD_IN_MILLISECONDS);
    }

    public Observable<List<BleDevice>> scan() {
        return Observable.create(new Observable.OnSubscribe<List<BleDevice>>() {
            @Override
            public void call(Subscriber<? super List<BleDevice>> subscriber) {
                mDevicesSubscriber = subscriber;
                discoveredDevices.refreshDiscoveredDevices();
                if (!scanner.isScanning()) {
                    scanner.start();
                    Log.d(TAG, "New scanner start");
                }
            }
        });
    }

    public void stop() {
        scanner.stop();
        discoveredDevices.clearDiscoveredDevices();
    }

    public boolean isScanning() {
        return scanner.isScanning();
    }

    @Override
    public void onModeSwitch(BleDeviceMode mode, BleDevice device) {
        if (discoveredDevices.isFullyConfigured(device.getAddress())) {
            mDevicesSubscriber.onNext(discoveredDevices.getAllConfiguredDevices());
        }
    }

    @Override
    public void onDeviceDiscovered(BleDevice device) {
        if (!discoveredDevices.isFullyConfigured(device.getAddress())) {
            discoveredDevices.addNewDevice(device.getAddress(), device);
            Log.d(TAG, "Device " + device.getName() + " added to discovered devices");
            mDevicesSubscriber.onNext(discoveredDevices.getAllConfiguredDevices());
        }
    }

    @Override
    public void onUnknownDeviceDiscovered(BleDevice device) {
        discoveredDevices.removeDevice(device);
    }
}
