package io.relayr.ble;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Build;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.UUID;

import static io.relayr.ble.BleDeviceCharacteristic.*;

@RunWith(RobolectricTestRunner.class)
public class BleDeviceCharacteristicTest {

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Test public void from_shortUuidInCharacteristic_shouldReturnSensorId() {
        UUID uuid = UUID.fromString("00002010-0000-1000-8000-00805f9b34fb");
        BluetoothGattCharacteristic characteristic = new BluetoothGattCharacteristic(uuid, 0, 0);
        BleDeviceCharacteristic bleDeviceCharacteristic =
                from(BleUtils.getShortUUID(characteristic.getUuid().toString()));
        Assert.assertEquals(SENSOR_ID, bleDeviceCharacteristic);
    }

    @Test public void from_shortUuidAsString_shouldReturnSensorId() {
        Assert.assertEquals(SENSOR_ID, BleDeviceCharacteristic.from("2010"));
    }

    @Test public void from_shortUuidAsString_shouldReturnPassKey() {
        Assert.assertEquals(PASS_KEY, BleDeviceCharacteristic.from("2018"));
    }

    @Test public void from_shortUuidAsString_shouldReturnOnBoardingFlag() {
        Assert.assertEquals(ON_BOARDING_FLAG, BleDeviceCharacteristic.from("2019"));
    }

    @Test public void from_shortUuidAsString_shouldReturnUnknown() {
        Assert.assertEquals(UNKNOWN, BleDeviceCharacteristic.from("1111"));
    }
}
