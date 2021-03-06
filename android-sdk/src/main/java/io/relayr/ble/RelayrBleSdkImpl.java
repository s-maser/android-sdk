package io.relayr.ble;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Build;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Func1;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
class RelayrBleSdkImpl extends RelayrBleSdk implements BleScannerFilter.BleFilteredScanCallback {

    private final BluetoothAdapter mBtAdapter;
    private final BleDeviceManager mDeviceManager;
    private final BleDevicesScanner mBleDeviceScanner;

    RelayrBleSdkImpl(BluetoothAdapter bluetoothAdapter, BleDeviceManager deviceManager) {
        mBtAdapter = bluetoothAdapter;
        mDeviceManager = deviceManager;
        BleScannerFilter mScannerFilter = new BleScannerFilter(mDeviceManager, this);
        mBleDeviceScanner = new BleDevicesScanner(bluetoothAdapter, mScannerFilter);
    }

    @Override
    public Observable<List<BleDevice>> scan(final Collection<BleDeviceType> deviceTypes) {
        return scan(deviceTypes, BleDevicesScanner.DEFAULT_SCAN_PERIOD);
    }

    @Override
    public Observable<List<BleDevice>> scan(final Collection<BleDeviceType> deviceTypes, final long scanPeriod) {
        final long key = System.currentTimeMillis();
        return Observable.create(new Observable.OnSubscribe<List<BleDevice>>() {
            @Override
            public void call(Subscriber<? super List<BleDevice>> subscriber) {
                mDeviceManager.addSubscriber(key, subscriber);
                mBleDeviceScanner.setScanPeriod(scanPeriod);
                mBleDeviceScanner.start();
            }
        }).filter(new Func1<List<BleDevice>, Boolean>() {
            @Override
            public Boolean call(List<BleDevice> bleDevices) {
                for (BleDevice device : bleDevices)
                    if (deviceTypes.contains(BleDeviceType.getDeviceType(device.getName())))
                        return true;
                return false;
            }
        }).map(new Func1<List<BleDevice>, List<BleDevice>>() {
            @Override
            public List<BleDevice> call(List<BleDevice> bleDevices) {
                List<BleDevice> devices = new ArrayList<>();
                for (BleDevice device : bleDevices)
                    if (deviceTypes.contains(BleDeviceType.getDeviceType(device.getName())))
                        devices.add(device);
                return devices;
            }
        }).doOnUnsubscribe(new Action0() {
            @Override
            public void call() {
                mDeviceManager.removeSubscriber(key);
                if (!mDeviceManager.isThereAnySubscriber()) mBleDeviceScanner.stop();
            }
        });
    }

    @Override
    public BleDevice getPairedDevice(final String macAddress) {
        for (BluetoothDevice bluetoothDevice : mBtAdapter.getBondedDevices()) {
            if (bluetoothDevice.getAddress().equals(macAddress))
                return new BleDevice(bluetoothDevice, macAddress,
                        BleDeviceMode.DIRECT_CONNECTION, mDeviceManager);
        }

        return null;
    }

    @Override
    public void stop() {
        mBleDeviceScanner.stop();
    }

    @Override
    public boolean isScanning() {
        return mBleDeviceScanner.isScanning();
    }

    @Override
    public void onLeScan(BleDevice device, int rssi) {
        device.setRssi(rssi);
        mDeviceManager.addDiscoveredDevice(device);
    }
}
