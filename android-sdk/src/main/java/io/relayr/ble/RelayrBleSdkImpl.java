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
class RelayrBleSdkImpl extends RelayrBleSdk {

    private static final String TAG = RelayrBleSdkImpl.class.toString();
    private static final int SCAN_PERIOD_IN_MILLISECONDS = 7000;

    private final BleDevicesScanner scanner;
    private final BleDeviceManager deviceManager = new BleDeviceManager();
    private Subscriber<? super List<BleDevice>> mDevicesSubscriber;
    private Collection<BleDeviceType> mDevicesInterestedIn = Collections.emptySet();

    RelayrBleSdkImpl() {
        BluetoothAdapter bluetoothAdapter = BleUtils.getBluetoothAdapter(RelayrApp.get());
        scanner = new BleDevicesScanner(bluetoothAdapter, new BluetoothAdapter.LeScanCallback() {

            private boolean hasAlreadyBeenDiscovered(BluetoothDevice device) {
                return deviceManager.isDeviceDiscovered(device.getAddress()) ||
                        !BleDeviceType.isKnownDevice(device.getAddress());
            }

            @Override
            public void onLeScan(BluetoothDevice device, final int rssi, byte[] scanRecord) {
                BleDeviceMode mode = BleDeviceMode.fromParcelUuidArray(device.getUuids());
                if (!mDevicesInterestedIn.contains(BleDeviceType.getDeviceType(device.getName())) ||
                        hasAlreadyBeenDiscovered(device) || mode.equals(UNKNOWN)) {
                    return;
                }
                deviceManager.addDiscoveredDevice(new BleDevice(device, device.getAddress(), mode));
                mDevicesSubscriber.onNext(deviceManager.getDiscoveredDevices());
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
                deviceManager.refreshConnectedDevices();
                if (!scanner.isScanning()) {
                    scanner.start();
                    Log.d(TAG, "New scanner start");
                }
            }
        });
    }

    public void stop() {
        scanner.stop();
        deviceManager.clear();
    }

    public boolean isScanning() {
        return scanner.isScanning();
    }
}
