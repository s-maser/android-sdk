package io.relayr.ble;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class BleDeviceTypeTest {

    @Test public void testGetDeviceTypeFromNullDevice() {
        Assert.assertEquals(BleDeviceType.getDeviceType(null), BleDeviceType.Unknown);
    }

    @Test public void testGetDeviceTypeFromRandomDevice() {
        Assert.assertEquals(BleDeviceType.getDeviceType("random text"), BleDeviceType.Unknown);
    }

}
