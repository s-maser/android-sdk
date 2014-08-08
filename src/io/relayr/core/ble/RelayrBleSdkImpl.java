package io.relayr.core.ble;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Build;
import android.util.Log;

import java.util.List;

import io.relayr.RelayrApp;
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
        BluetoothAdapter bluetoothAdapter = BleUtils.getBluetoothAdapter(RelayrApp.get());
        scanner = new BleDevicesScanner(bluetoothAdapter, new BluetoothAdapter.LeScanCallback() {

            private boolean hasAlreadyBeenDiscovered(BluetoothDevice device) {
                return discoveredDevices.isDeviceDiscovered(device.getAddress()) ||
                        BleDeviceType.getDeviceType(device.getName()) == BleDeviceType.Unknown;
            }

            @Override
            public void onLeScan(BluetoothDevice device, final int rssi, byte[] scanRecord) {
                if (hasAlreadyBeenDiscovered(device)) return;
                BleDevice relayrDevice = new BleDevice(device, RelayrBleSdkImpl.this);
                relayrDevice.connect();
                // TODO: ADD DISCOVERED BUT NOT CONFIGURED DEVICES INSTEAD OF NULL VALUES
                discoveredDevices.addNewDevice(relayrDevice.getAddress(), null);
                Log.d(TAG, "Configuring New device: "+ device.getName() + " [" + device.getAddress() + "]");
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
    public void onDeviceConnectedToMasterModuleDiscovered(BleDevice device) {
        discoveredDevices.removeDevice(device);
    }
}
