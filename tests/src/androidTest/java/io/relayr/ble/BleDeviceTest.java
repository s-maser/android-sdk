package io.relayr.ble;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.os.Build;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static android.bluetooth.BluetoothGatt.GATT_FAILURE;
import static android.bluetooth.BluetoothGatt.GATT_REQUEST_NOT_SUPPORTED;
import static io.relayr.ble.BleDeviceMode.ON_BOARDING;
import static io.relayr.ble.BleDeviceMode.DIRECT_CONNECTION;
import static io.relayr.ble.BleShortUUID.CHARACTERISTIC_SENSOR_ID;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
@RunWith(RobolectricTestRunner.class)
public class BleDeviceTest {

    private static final byte[] SENSOR_ID = new byte[] {0x11, 0x11, 0x11, 0x11, 0x11, 0x11, 0x11,
            0x11, 0x11, 0x11, 0x11, 0x11,0x11, 0x11, 0x11, 0x11};
    private static final byte[] PASS_KEY = new byte[]{0x11, 0x11, 0x11, 0x11, 0x11, 0x11};

    private static final UUID UUID_UUID_SENSOR_ID =
            UUID.fromString("00002010-0000-1000-8000-00805f9b34fb");
    private static final UUID UUID_PASS_KEY =
            UUID.fromString("00002018-0000-1000-8000-00805f9b34fb");
    private static final UUID UUID_ON_BOARDING_FLAG =
            UUID.fromString("00002019-0000-1000-8000-00805f9b34fb");

    private BluetoothGattCharacteristic characteristic1 =
            new BluetoothGattCharacteristic(UUID_UUID_SENSOR_ID, 0, 0);
    private final BluetoothGattCharacteristic characteristic2 =
            new BluetoothGattCharacteristic(UUID_PASS_KEY, 0, 0);
    private final BluetoothGattCharacteristic characteristic3 =
            new BluetoothGattCharacteristic(UUID_ON_BOARDING_FLAG, 0, 0);

    private final List<BluetoothGattCharacteristic> characteristics = new ArrayList<>();

    private BleDevice mDevice;
    private BluetoothGattService mService;

    @Before public void init() {
        characteristics.add(characteristic1);
        characteristics.add(characteristic2);
        characteristics.add(characteristic3);
        BluetoothDevice d = mock(BluetoothDevice.class);
        mDevice = new BleDevice(d, "dd", "WunderbarHTU", ON_BOARDING);

        mService = mock(BluetoothGattService.class);
    }

    @Test public void writeSensorId_shouldWork() {

        when(mService.getCharacteristics()).thenReturn(characteristics);
        Assert.assertEquals(3, mService.getCharacteristics().size());

        mDevice.setBluetoothGattService(mService);

        BleDeviceConnectionCallback callback = mock(BleDeviceConnectionCallback.class);

        mDevice.connect(callback);

        mDevice.gatt = mock(BluetoothGatt.class);
        mDevice.writeSensorId(SENSOR_ID);

        Assert.assertTrue(mDevice.isConnected());

        verify(mDevice.gatt, times(1)).writeCharacteristic(characteristic1);

    }

    @Test public void writeSensorId_shouldFail_becauseThereAreNoCharacteristics() {

        mDevice.setBluetoothGattService(mService);

        BleDeviceConnectionCallback callback = mock(BleDeviceConnectionCallback.class);

        mDevice.connect(callback);
        mDevice.gatt = mock(BluetoothGatt.class);
        mDevice.writeSensorId(SENSOR_ID);

        Assert.assertTrue(mDevice.isConnected());

        verify(callback, times(1)).onWriteError(mDevice,
                BleDeviceCharacteristic.from(CHARACTERISTIC_SENSOR_ID), GATT_REQUEST_NOT_SUPPORTED);
    }

    @Test public void writeSensorId_shouldFail_becauseTheServiceHasNotBeenInitialised() {

        BleDeviceConnectionCallback callback = mock(BleDeviceConnectionCallback.class);
        mDevice.connect(callback);
        mDevice.gatt = mock(BluetoothGatt.class);
        mDevice.writeSensorId(SENSOR_ID);

        Assert.assertTrue(mDevice.isConnected());

        verify(callback, times(1)).onWriteError(
                mDevice, BleDeviceCharacteristic.from(CHARACTERISTIC_SENSOR_ID), GATT_FAILURE);
    }

    @Test public void writeSensorId_shouldFail_becauseTheDeviceIsNotInOnBoardingMode() {
        BluetoothDevice d = mock(BluetoothDevice.class);
        BleDevice device = new BleDevice(d, "dd", "WunderbarHTU", DIRECT_CONNECTION);


        BleDeviceConnectionCallback callback = mock(BleDeviceConnectionCallback.class);
        device.connect(callback);
        device.writeSensorId(SENSOR_ID);

        verify(callback, times(1)).onWriteError(device,
                BleDeviceCharacteristic.from(CHARACTERISTIC_SENSOR_ID), GATT_REQUEST_NOT_SUPPORTED);
    }

    @Test public void writePassKey_shouldWork() {

        when(mService.getCharacteristics()).thenReturn(characteristics);
        Assert.assertEquals(3, mService.getCharacteristics().size());

        mDevice.setBluetoothGattService(mService);

        BleDeviceConnectionCallback callback = mock(BleDeviceConnectionCallback.class);

        mDevice.connect(callback);

        mDevice.gatt = mock(BluetoothGatt.class);
        mDevice.writePassKey(PASS_KEY);

        Assert.assertTrue(mDevice.isConnected());

        verify(mDevice.gatt, times(1)).writeCharacteristic(characteristic2);
    }

    @Test public void writeOnBoardingFlag_shouldWork() {

        when(mService.getCharacteristics()).thenReturn(characteristics);
        Assert.assertEquals(3, mService.getCharacteristics().size());

        mDevice.setBluetoothGattService(mService);

        BleDeviceConnectionCallback callback = mock(BleDeviceConnectionCallback.class);

        mDevice.connect(callback);

        mDevice.gatt = mock(BluetoothGatt.class);
        mDevice.writeOnBoardingFlag(new byte[] {1});

        Assert.assertTrue(mDevice.isConnected());

        verify(mDevice.gatt, times(1)).writeCharacteristic(characteristic3);
    }

}
