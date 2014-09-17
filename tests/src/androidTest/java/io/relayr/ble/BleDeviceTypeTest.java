package io.relayr.ble;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static io.relayr.ble.BleDeviceType.*;

@RunWith(RobolectricTestRunner.class)
public class BleDeviceTypeTest {

    @Test public void testGetDeviceTypeFromNullText() {
        Assert.assertEquals(getDeviceType(null), Unknown);
    }

    @Test public void testGetDeviceTypeFromRandomText() {
        Assert.assertEquals(getDeviceType("random text"), Unknown);
    }

    @Test public void testGetDeviceTypeFromThermometerText() {
        Assert.assertEquals(getDeviceType("WunderbarHTU"), WunderbarHTU);
    }

    @Test public void testGetDeviceTypeFromGyroscopeText() {
        Assert.assertEquals(getDeviceType("WunderbarGYRO"), WunderbarGYRO);
    }

    @Test public void testGetDeviceTypeFromLightText() {
        Assert.assertEquals(getDeviceType("WunderbarLIGHT"), WunderbarLIGHT);
    }

    @Test public void testGetDeviceTypeFromMicrophoneText() {
        Assert.assertEquals(getDeviceType("WunderbarMIC"), WunderbarMIC);
    }

    @Test public void testGetDeviceTypeFromBridgeText() {
        Assert.assertEquals(getDeviceType("WunderbarBRIDG"), WunderbarBRIDG);
    }

    @Test public void testGetDeviceTypeFromInfraredText() {
        Assert.assertEquals(getDeviceType("WunderbarIR"), WunderbarIR);
    }

    @Test public void testGetDeviceTypeFromApplicationText() {
        Assert.assertEquals(getDeviceType("WunderbarApp"), WunderbarApp);
    }

    @Test public void isKnownDevice_shouldBeTrue() {
        Assert.assertTrue(isKnownDevice("WunderbarIR"));
    }

    @Test public void isKnownDevice_shouldBeFalse() {
        Assert.assertFalse(isKnownDevice("random device"));
    }

}
