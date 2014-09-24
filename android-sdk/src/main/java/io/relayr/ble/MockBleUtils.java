package io.relayr.ble;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.content.pm.PackageManager;
import android.os.Build;

import javax.inject.Inject;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class MockBleUtils extends BleUtils {

    @Inject public MockBleUtils(BluetoothAdapter bluetoothAdapter, PackageManager packageManager) {
        super(bluetoothAdapter, packageManager);
    }

    @Override
    public boolean isBleAvailable() {
        return true;
    }

    @Override
    public boolean isBleSupported() {
        return super.isSdk18() && isBleSupportedIndeed();
    }

    @Override
    protected boolean isBleSupportedIndeed() {
        return true;
    }

    @Override
    protected int getBleStatus() {
        return STATUS_BLE_ENABLED;
    }

}
