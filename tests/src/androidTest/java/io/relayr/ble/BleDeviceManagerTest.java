package io.relayr.ble;

import android.bluetooth.BluetoothDevice;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;

import static io.relayr.ble.BleDeviceMode.ON_BOARDING;

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
        BleDevice device = new BleDevice(Mockito.mock(BluetoothDevice.class), "bla", ON_BOARDING);
        deviceManager.addDiscoveredDevice(device);
        Assert.assertEquals(1, deviceManager.getDiscoveredDevices().size());
    }

    @Test public void clearTest() {
        BleDeviceManager deviceManager = new BleDeviceManager();
        BleDevice device = new BleDevice(Mockito.mock(BluetoothDevice.class),  "bla", ON_BOARDING);
        deviceManager.addDiscoveredDevice(device);
        Assert.assertEquals(1, deviceManager.getDiscoveredDevices().size());
        deviceManager.clear();
        Assert.assertEquals(0, deviceManager.getDiscoveredDevices().size());
    }

    @Test public void isDeviceDiscoveredTest() {
        BleDeviceManager deviceManager = new BleDeviceManager();
        BleDevice device = new BleDevice(Mockito.mock(BluetoothDevice.class),  "bla", ON_BOARDING);
        deviceManager.addDiscoveredDevice(device);
        Assert.assertTrue(deviceManager.isDeviceDiscovered(device));
    }

    @Test public void removeDeviceTest() {
        BleDeviceManager deviceManager = new BleDeviceManager();
        BleDevice device = new BleDevice(Mockito.mock(BluetoothDevice.class),  "bla", ON_BOARDING);
        deviceManager.addDiscoveredDevice(device);
        Assert.assertEquals(1, deviceManager.getDiscoveredDevices().size());
        deviceManager.removeDevice(device);
        Assert.assertEquals(0, deviceManager.getDiscoveredDevices().size());
    }
}
