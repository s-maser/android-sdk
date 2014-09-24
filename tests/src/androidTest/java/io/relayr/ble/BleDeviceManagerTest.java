package io.relayr.ble;

import android.bluetooth.BluetoothDevice;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;

import java.util.List;

import rx.Subscriber;

import static io.relayr.ble.BleDeviceMode.ON_BOARDING;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
public class BleDeviceManagerTest {

    @Test public void noDiscoveredDevicesOnCreationTest() {
        BleDeviceManager deviceManager = new BleDeviceManager();
        Assert.assertEquals(0, deviceManager.getDiscoveredDevices().size());
    }

    @Test public void noConnectedDevicesOnCreationTest() {
        BleDeviceManager deviceManager = new BleDeviceManager();
        Assert.assertEquals(0, deviceManager.getDiscoveredDevices().size());
    }

    @Test public void addDiscoveredDeviceTest() {
        BleDeviceManager deviceManager = new BleDeviceManager();
        BleDevice device = new BleDevice(Mockito.mock(BluetoothDevice.class), "", "", ON_BOARDING);
        deviceManager.addDiscoveredDevice(device);
        Assert.assertEquals(1, deviceManager.getDiscoveredDevices().size());
    }

    @Test public void addDiscoveredDeviceTest_shouldCallOnNext() {
        @SuppressWarnings("unchecked")
        Subscriber<? super List<BleDevice>> devicesSubscriber = Mockito.mock(Subscriber.class);
        BleDeviceManager deviceManager = new BleDeviceManager();
        deviceManager.init(devicesSubscriber);

        BleDevice device = new BleDevice(Mockito.mock(BluetoothDevice.class), "", "", ON_BOARDING);
        deviceManager.addDiscoveredDevice(device);

        verify(devicesSubscriber, times(1)).onNext(deviceManager.getDiscoveredDevices());
    }

    @Test public void clearTest() {
        BleDeviceManager deviceManager = new BleDeviceManager();
        BleDevice device = new BleDevice(Mockito.mock(BluetoothDevice.class), "", "", ON_BOARDING);
        deviceManager.addDiscoveredDevice(device);
        Assert.assertEquals(1, deviceManager.getDiscoveredDevices().size());
        deviceManager.clear();
        Assert.assertEquals(0, deviceManager.getDiscoveredDevices().size());
    }

    @Test public void isDeviceDiscoveredTest() {
        BleDeviceManager deviceManager = new BleDeviceManager();
        BleDevice device = new BleDevice(Mockito.mock(BluetoothDevice.class), "", "", ON_BOARDING);
        deviceManager.addDiscoveredDevice(device);
        Assert.assertTrue(deviceManager.isDeviceDiscovered(device));
    }

    @Test public void removeDeviceTest() {
        BleDeviceManager deviceManager = new BleDeviceManager();
        BleDevice device = new BleDevice(Mockito.mock(BluetoothDevice.class), "", "",  ON_BOARDING);
        deviceManager.addDiscoveredDevice(device);
        Assert.assertEquals(1, deviceManager.getDiscoveredDevices().size());
        deviceManager.removeDevice(device);
        Assert.assertEquals(0, deviceManager.getDiscoveredDevices().size());
    }
}
