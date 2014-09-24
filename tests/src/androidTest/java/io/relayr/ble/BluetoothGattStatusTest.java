package io.relayr.ble;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothGatt;
import android.os.Build;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static android.bluetooth.BluetoothGatt.*;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
@RunWith(RobolectricTestRunner.class)
public class BluetoothGattStatusTest {

    @Test public void toString_ofARandomStatus_shouldNotBeNull() {
        Assert.assertNotNull(BluetoothGattStatus.toString(-1));
    }

    @Test public void toString_ofAKnownStatus_shouldNotBeNull() {
        Assert.assertNotNull(BluetoothGattStatus.toString(GATT_FAILURE));
        Assert.assertNotNull(BluetoothGattStatus.toString(GATT_INSUFFICIENT_AUTHENTICATION));
        Assert.assertNotNull(BluetoothGattStatus.toString(GATT_INSUFFICIENT_ENCRYPTION));
        Assert.assertNotNull(BluetoothGattStatus.toString(GATT_INVALID_ATTRIBUTE_LENGTH));
        Assert.assertNotNull(BluetoothGattStatus.toString(GATT_INVALID_OFFSET));
        Assert.assertNotNull(BluetoothGattStatus.toString(GATT_READ_NOT_PERMITTED));
        Assert.assertNotNull(BluetoothGattStatus.toString(GATT_REQUEST_NOT_SUPPORTED));
        Assert.assertNotNull(BluetoothGattStatus.toString(GATT_SUCCESS));
        Assert.assertNotNull(BluetoothGattStatus.toString(GATT_WRITE_NOT_PERMITTED));
    }

    @Test public void isFailureStatus_shouldBeTrue() {
        Assert.assertTrue(BluetoothGattStatus.isFailureStatus(BluetoothGatt.GATT_FAILURE));
    }

    @Test public void isFailureStatus_shouldBeFalse() {
        Assert.assertFalse(BluetoothGattStatus.isFailureStatus(BluetoothGatt.GATT_SUCCESS));
    }

}
