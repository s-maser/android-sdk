package io.relayr.ble;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Build;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import io.relayr.ble.parser.AdvertisementPacketParser;

import static io.relayr.ble.BleDeviceMode.UNKNOWN;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
class BleScannerFilter implements BluetoothAdapter.LeScanCallback {

    private Collection<BleDeviceType> mDevicesInterestedIn = Collections.emptySet();
    private final BleDeviceManager mDeviceManager;
    private final BleFilteredScanCallback mBleFilteredScanCallback;

    BleScannerFilter(BleDeviceManager deviceManager, BleFilteredScanCallback callback) {
        mDeviceManager = deviceManager;
        mBleFilteredScanCallback = callback;
    }

    boolean isRelevant(BluetoothDevice device, BleDeviceMode mode) {
        return !mDeviceManager.isDeviceDiscovered(device.getAddress()) &&
                BleDeviceType.isKnownDevice(device.getName()) &&
                mDevicesInterestedIn.contains(BleDeviceType.getDeviceType(device.getName())) &&
                !mode.equals(UNKNOWN);
    }

    void setDevicesInterestedIn(Collection<BleDeviceType> devicesInterestedIn) {
        if (devicesInterestedIn != null) mDevicesInterestedIn = devicesInterestedIn;
    }

    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        String deviceName = AdvertisementPacketParser.decodeDeviceName(scanRecord);
        List<String> serviceUuids = AdvertisementPacketParser.decodeServicesUuid(scanRecord);
        BleDeviceMode mode = BleDeviceMode.fromServiceUuids(serviceUuids);
        if (!isRelevant(device, mode)) return;
        BleDevice bleDevice = new BleDevice(device, device.getAddress(), deviceName, mode);
        if (mBleFilteredScanCallback != null) mBleFilteredScanCallback.onLeScan(bleDevice, rssi);
    }

    interface BleFilteredScanCallback {
        public void onLeScan(BleDevice device, int rssi);
    }

}
