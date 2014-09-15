package io.relayr.ble;

import android.bluetooth.BluetoothDevice;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class BleDeviceManagerTest {

    @Test public void noDiscoveredDevicesOnCreationTest() {
        BleDeviceManager deviceManager = new BleDeviceManager();
        Assert.assertEquals(0, deviceManager.getConnectedDevices().size());
    }

    @Test public void noConnectedDevicesOnCreationTest() {
        BleDeviceManager deviceManager = new BleDeviceManager();
        Assert.assertEquals(0, deviceManager.getConnectedDevices().size());
    }

    @Test public void addAConnectedDeviceTest() {
        BleDeviceManager deviceManager = new BleDeviceManager();
        BleDevice device = new BleDevice(Mockito.mock(BluetoothDevice.class),
                Mockito.mock(BleDeviceEventCallback.class), "bla");
        deviceManager.addNewConnectedDevice(device);
        Assert.assertEquals(1, deviceManager.getConnectedDevices().size());
    }

    @Test public void clearConnectedDevicesTest() {
        BleDeviceManager deviceManager = new BleDeviceManager();
        BleDevice device = new BleDevice(Mockito.mock(BluetoothDevice.class),
                Mockito.mock(BleDeviceEventCallback.class), "bla");
        deviceManager.addNewConnectedDevice(device);
        Assert.assertEquals(1, deviceManager.getConnectedDevices().size());
        deviceManager.clear();
        Assert.assertEquals(0, deviceManager.getConnectedDevices().size());
    }

    @Test public void isDiscoveredDeviceConnectedTest() {
        BleDeviceManager deviceManager = new BleDeviceManager();
        BleDevice device = new BleDevice(Mockito.mock(BluetoothDevice.class),
                Mockito.mock(BleDeviceEventCallback.class), "bla");
        deviceManager.addNewConnectedDevice(device);
        Assert.assertTrue(deviceManager.isDiscoveredDeviceConnected(device));
    }

    @Test public void isDeviceDiscoveredTest() {
        BleDeviceManager deviceManager = new BleDeviceManager();
        BleDevice device = new BleDevice(Mockito.mock(BluetoothDevice.class),
                Mockito.mock(BleDeviceEventCallback.class), "bla");
        deviceManager.addNewDiscoveredDevice(device);
        Assert.assertTrue(deviceManager.isDeviceDiscovered(device));
    }

    @Test public void removeDeviceTest() {
        BleDeviceManager deviceManager = new BleDeviceManager();
        BleDevice device = new BleDevice(Mockito.mock(BluetoothDevice.class),
                Mockito.mock(BleDeviceEventCallback.class), "bla");
        deviceManager.addNewConnectedDevice(device);
        Assert.assertEquals(1, deviceManager.getConnectedDevices().size());
        deviceManager.removeDevice(device);
        Assert.assertEquals(0, deviceManager.getConnectedDevices().size());
    }
}
