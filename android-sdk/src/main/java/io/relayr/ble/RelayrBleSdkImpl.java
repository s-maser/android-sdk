package io.relayr.ble;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.os.Build;

import java.util.Collection;
import java.util.List;

import io.relayr.RelayrApp;
import rx.Observable;
import rx.Subscriber;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
class RelayrBleSdkImpl extends RelayrBleSdk implements BleScannerFilter.BleFilteredScanCallback {

    private final BleDevicesScanner mBleDeviceScanner;
    private final BleDeviceManager mDeviceManager = new BleDeviceManager();
    private final BleScannerFilter mScannerFilter;

    RelayrBleSdkImpl() {
        BluetoothAdapter bluetoothAdapter = BleUtils.getBluetoothAdapter(RelayrApp.get());
        mScannerFilter = new BleScannerFilter(mDeviceManager, this);
        mBleDeviceScanner = new BleDevicesScanner(bluetoothAdapter, mScannerFilter);
    }

    public Observable<List<BleDevice>> scan(final Collection<BleDeviceType> deviceTypes) {
        return Observable.create(new Observable.OnSubscribe<List<BleDevice>>() {
            @Override
            public void call(Subscriber<? super List<BleDevice>> subscriber) {
                mScannerFilter.setDevicesInterestedIn(deviceTypes);
                mDeviceManager.init(subscriber);
                mBleDeviceScanner.start();
            }
        });
    }

    public void stop() {
        mBleDeviceScanner.stop();
        mDeviceManager.clear();
    }

    public boolean isScanning() {
        return mBleDeviceScanner.isScanning();
    }

    @Override
    public void onLeScan(BleDevice device, int rssi) {
        mDeviceManager.addDiscoveredDevice(device);
    }
}
