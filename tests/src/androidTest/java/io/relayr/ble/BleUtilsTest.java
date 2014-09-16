package io.relayr.ble;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class BleUtilsTest {

    @Test public void getShortUuidTest() {
        Assert.assertEquals(BleUtils.getShortUUID("00002002-0000-1000-8000-00805f9b34fb"), "2002");
    }

}
