package io.relayr.ble;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Build;

import java.util.List;

import io.relayr.ble.parser.AdvertisementPacketParser;

import static io.relayr.ble.BleDeviceMode.NEW_ON_BOARDING;
import static io.relayr.ble.BleDeviceMode.UNKNOWN;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
class BleScannerFilter implements BluetoothAdapter.LeScanCallback {

    private final BleDeviceManager mDeviceManager;
    private final BleFilteredScanCallback mBleFilteredScanCallback;

    BleScannerFilter(BleDeviceManager deviceManager, BleFilteredScanCallback callback) {
        mDeviceManager = deviceManager;
        mBleFilteredScanCallback = callback;
    }

    boolean isRelevant(BluetoothDevice device, String deviceName, BleDeviceMode mode) {
        return !mDeviceManager.isDeviceDiscovered(device, mode) &&
                BleDeviceType.isKnownDevice(deviceName) &&
                !mode.equals(UNKNOWN);
    }

    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        String deviceName = AdvertisementPacketParser.decodeDeviceName(scanRecord);

        if (deviceName != null && deviceName.contains("Wunderbar MM")) {
            BleDevice bleDevice = new BleDevice(device, deviceName, NEW_ON_BOARDING, mDeviceManager, rssi);
            if (mBleFilteredScanCallback != null)
                mBleFilteredScanCallback.onLeScan(bleDevice, rssi);
            return;
        }

        List<String> serviceUuids = AdvertisementPacketParser.decodeServicesUuid(scanRecord);
        BleDeviceMode mode = BleDeviceMode.fromServiceUuids(serviceUuids);
        if (!isRelevant(device, deviceName, mode)) return;

        BleDevice bleDevice = new BleDevice(device, deviceName, mode, mDeviceManager, rssi);
        if (mBleFilteredScanCallback != null) mBleFilteredScanCallback.onLeScan(bleDevice, rssi);
    }

    interface BleFilteredScanCallback {
        void onLeScan(BleDevice device, int rssi);
    }
}
