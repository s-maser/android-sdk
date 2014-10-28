package io.relayr.ble;

import android.bluetooth.BluetoothDevice;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.List;

import rx.Subscriber;

import static io.relayr.ble.BleDeviceMode.ON_BOARDING;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
public class BleDeviceManagerTest {

    private BleDevice device = new BleDevice(mock(BluetoothDevice.class), "", "", ON_BOARDING);

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

        deviceManager.addDiscoveredDevice(device);
        Assert.assertEquals(1, deviceManager.getDiscoveredDevices().size());
    }

    @Test public void addDiscoveredDeviceTest_shouldCallOnNext() {
        @SuppressWarnings("unchecked")
        Subscriber<? super List<BleDevice>> devicesSubscriber = mock(Subscriber.class);
        BleDeviceManager deviceManager = new BleDeviceManager();
        deviceManager.addSubscriber(System.currentTimeMillis(), devicesSubscriber);

        deviceManager.addDiscoveredDevice(device);

        verify(devicesSubscriber, times(1)).onNext(deviceManager.getDiscoveredDevices());
    }

    @Test public void addDiscoveredDevice_forTheSecondTime_shouldCallOnNext() {
        @SuppressWarnings("unchecked")
        Subscriber<? super List<BleDevice>> devicesSubscriber = mock(Subscriber.class);
        BleDeviceManager deviceManager = new BleDeviceManager();
        deviceManager.addSubscriber(System.currentTimeMillis(), devicesSubscriber);

        deviceManager.addDiscoveredDevice(device);
        deviceManager.addDiscoveredDevice(device);

        verify(devicesSubscriber, times(2)).onNext(deviceManager.getDiscoveredDevices());
    }

    @Test public void after_removeSubscriber_isCalled_theSubscriberShouldNotBeNotified() {
        @SuppressWarnings("unchecked")
        Subscriber<? super List<BleDevice>> devicesSubscriber = mock(Subscriber.class);
        BleDeviceManager deviceManager = new BleDeviceManager();
        long time = System.currentTimeMillis();

        deviceManager.addSubscriber(time, devicesSubscriber);
        deviceManager.removeSubscriber(time);

        deviceManager.addDiscoveredDevice(device);
        verify(devicesSubscriber, never()).onNext(anyListOf(BleDevice.class));
    }

    @Test public void clearTest() {
        BleDeviceManager deviceManager = new BleDeviceManager();
        deviceManager.addDiscoveredDevice(device);
        Assert.assertEquals(1, deviceManager.getDiscoveredDevices().size());
        deviceManager.clear();
        Assert.assertEquals(0, deviceManager.getDiscoveredDevices().size());
    }

    @Test public void isDeviceDiscoveredTest() {
        BleDeviceManager deviceManager = new BleDeviceManager();
        deviceManager.addDiscoveredDevice(device);
        Assert.assertTrue(deviceManager.isDeviceDiscovered(device));
    }

    @Test public void removeDeviceTest() {
        BleDeviceManager deviceManager = new BleDeviceManager();
        deviceManager.addDiscoveredDevice(device);
        Assert.assertEquals(1, deviceManager.getDiscoveredDevices().size());
        deviceManager.removeDevice(device);
        Assert.assertEquals(0, deviceManager.getDiscoveredDevices().size());
    }
}
