package io.relayr.ble;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Build;
import android.util.Log;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import io.relayr.RelayrApp;
import rx.Observable;
import rx.Subscriber;

import static io.relayr.ble.BleDeviceMode.UNKNOWN;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
class RelayrBleSdkImpl extends RelayrBleSdk implements BleDeviceEventCallback {

    private static final String TAG = RelayrBleSdkImpl.class.toString();
    private static final int SCAN_PERIOD_IN_MILLISECONDS = 7000;

    private final BleDevicesScanner scanner;
    private final BleDeviceManager discoveredDevices = new BleDeviceManager();
    private Subscriber<? super List<BleDevice>> mDevicesSubscriber;
    private Collection<BleDeviceType> mDevicesInterestedIn = Collections.emptySet();

    RelayrBleSdkImpl() {
        BluetoothAdapter bluetoothAdapter = BleUtils.getBluetoothAdapter(RelayrApp.get());
        scanner = new BleDevicesScanner(bluetoothAdapter, new BluetoothAdapter.LeScanCallback() {

            private boolean hasAlreadyBeenDiscovered(BluetoothDevice device) {
                return discoveredDevices.isDeviceDiscovered(device.getAddress()) ||
                        BleDeviceType.getDeviceType(device.getName()) == BleDeviceType.Unknown;
            }

            @Override
            public void onLeScan(BluetoothDevice device, final int rssi, byte[] scanRecord) {
                BleDeviceMode mode = BleDeviceMode.fromParcelUuidArray(device.getUuids());
                if (!mDevicesInterestedIn.contains(BleDeviceType.getDeviceType(device.getName())) ||
                        hasAlreadyBeenDiscovered(device) || mode.equals(UNKNOWN)) {
                    return;
                }
                BleDevice relayrDevice = new BleDevice(device, RelayrBleSdkImpl.this, device.getAddress(), mode);
                relayrDevice.connect();
                discoveredDevices.addNewDiscoveredDevice(relayrDevice);
                Log.d(TAG, "Configuring New device: "+ device.getName() + " [" + device.getAddress() + "]");
            }
        });
        scanner.setScanPeriod(SCAN_PERIOD_IN_MILLISECONDS);
    }

    public Observable<List<BleDevice>> scan(final Collection<BleDeviceType> deviceTypes) {
        return Observable.create(new Observable.OnSubscribe<List<BleDevice>>() {
            @Override
            public void call(Subscriber<? super List<BleDevice>> subscriber) {
                if (deviceTypes != null) mDevicesInterestedIn = deviceTypes;
                mDevicesSubscriber = subscriber;
                discoveredDevices.refreshConnectedDevices();
                if (!scanner.isScanning()) {
                    scanner.start();
                    Log.d(TAG, "New scanner start");
                }
            }
        });
    }

    public void stop() {
        scanner.stop();
        discoveredDevices.clear();
    }

    public boolean isScanning() {
        return scanner.isScanning();
    }

    @Override
    public void onModeSwitch(BleDeviceMode mode, BleDevice device) {
        if (discoveredDevices.isDiscoveredDeviceConnected(device)) {
            mDevicesSubscriber.onNext(discoveredDevices.getConnectedDevices());
        }
    }

    @Override
    public void onConnectedDeviceDiscovered(BleDevice device) {
        if (!discoveredDevices.isDiscoveredDeviceConnected(device)) {
            discoveredDevices.addNewConnectedDevice(device);
            Log.d(TAG, "Device " + device.getName() + " added to connected devices");
            mDevicesSubscriber.onNext(discoveredDevices.getConnectedDevices());
        }
    }
}
