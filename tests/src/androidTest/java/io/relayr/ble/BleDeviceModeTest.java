package io.relayr.ble;

import android.os.ParcelUuid;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.UUID;

import static io.relayr.ble.BleDeviceMode.CONNECTED_TO_MASTER_MODULE;
import static io.relayr.ble.BleDeviceMode.DIRECT_CONNECTION;
import static io.relayr.ble.BleDeviceMode.ON_BOARDING;
import static io.relayr.ble.BleDeviceMode.UNKNOWN;
import static io.relayr.ble.BleDeviceMode.containsService;
import static io.relayr.ble.BleDeviceMode.fromParcelUuidArray;
import static io.relayr.ble.BleDeviceMode.fromUuid;

@RunWith(RobolectricTestRunner.class)
public class BleDeviceModeTest {

    @Test public void fromUuid_shouldReturn_connectedToMasterModule() {
        Assert.assertEquals(CONNECTED_TO_MASTER_MODULE, fromUuid("2000"));
    }

    @Test public void fromUuid_shouldReturn_onBoarding() {
        Assert.assertEquals(ON_BOARDING, fromUuid("2001"));
    }

    @Test public void fromUuid_shouldReturn_directConnection() {
        Assert.assertEquals(DIRECT_CONNECTION, fromUuid("2002"));
    }

    @Test public void fromParcelUuidArrayTest_fromEmptyArray_shouldReturnUnknown() {
        Assert.assertEquals(UNKNOWN, fromParcelUuidArray(new ParcelUuid[0]));
    }

    @Test public void fromParcelUuidArrayTest_fromRandomUuidArray_shouldReturnUnknown() {
        ParcelUuid[] uuids = new ParcelUuid[] {new ParcelUuid(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"))};
        Assert.assertEquals(UNKNOWN, fromParcelUuidArray(uuids));
    }

    @Test public void fromParcelUuidArrayTest_containingOnBoardingService_shouldReturnOnBoarding() {
        ParcelUuid uuid1 = new ParcelUuid(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));
        ParcelUuid uuid2 = new ParcelUuid(UUID.fromString("00002001-0000-1000-8000-00805f9b34fb"));
        ParcelUuid[] uuids = new ParcelUuid[] {uuid1, uuid2};
        Assert.assertEquals(ON_BOARDING, fromParcelUuidArray(uuids));
    }

    @Test public void containsService_shouldReturnFalse() {
        Assert.assertFalse(containsService("1"));
    }

    @Test public void containsService_shouldReturnTrue() {
        Assert.assertTrue(containsService("2000"));
    }

}
