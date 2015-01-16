package io.relayr.ble;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.os.Build;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;

import static android.bluetooth.BluetoothAdapter.*;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
@RunWith(RobolectricTestRunner.class)
public class BleDeviceScannerTest {

    @Test public void isScanning_afterCreation_shouldReturnFalse() {
        BluetoothAdapter adapter = Mockito.mock(BluetoothAdapter.class);
        LeScanCallback callback = Mockito.mock(LeScanCallback.class);
        BleDevicesScanner bleDevicesScanner = new BleDevicesScanner(adapter, callback);
        Assert.assertFalse(bleDevicesScanner.isScanning());
    }

    @Test public void isScanning_afterStart_shouldReturnTrue() {
        BluetoothAdapter adapter = Mockito.mock(BluetoothAdapter.class);
        LeScanCallback callback = Mockito.mock(LeScanCallback.class);
        BleDevicesScanner bleDevicesScanner = new BleDevicesScanner(adapter, callback);
        bleDevicesScanner.start();
        Assert.assertTrue(bleDevicesScanner.isScanning());
    }

    @Test public void isScanning_afterStartingTwice_shouldReturnTrue() {
        BluetoothAdapter adapter = Mockito.mock(BluetoothAdapter.class);
        LeScanCallback callback = Mockito.mock(LeScanCallback.class);
        BleDevicesScanner bleDevicesScanner = new BleDevicesScanner(adapter, callback);
        bleDevicesScanner.start();
        bleDevicesScanner.start();
        Assert.assertTrue(bleDevicesScanner.isScanning());
    }

    @Test public void isScanning_afterStartAndStop_shouldReturnFalse() {
        BluetoothAdapter adapter = Mockito.mock(BluetoothAdapter.class);
        LeScanCallback callback = Mockito.mock(LeScanCallback.class);
        BleDevicesScanner bleDevicesScanner = new BleDevicesScanner(adapter, callback);
        bleDevicesScanner.start();
        bleDevicesScanner.stop();
        Assert.assertFalse(bleDevicesScanner.isScanning());
    }

    @Test public void isScanning_afterStartCustomTimeout_shouldStop() {
        BluetoothAdapter adapter = Mockito.mock(BluetoothAdapter.class);
        LeScanCallback callback = Mockito.mock(LeScanCallback.class);
        BleDevicesScanner bleDevicesScanner = new BleDevicesScanner(adapter, callback);
        bleDevicesScanner.setScanPeriod(20);
        bleDevicesScanner.start();
        Assert.assertTrue(bleDevicesScanner.isScanning());
        Mockito.verify(adapter, Mockito.timeout(200).atLeastOnce()).stopLeScan(bleDevicesScanner);
    }
}
