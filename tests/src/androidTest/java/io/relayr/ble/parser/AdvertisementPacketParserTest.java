package io.relayr.ble.parser;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.List;

@RunWith(RobolectricTestRunner.class)
public class AdvertisementPacketParserTest {

    private byte[] data = new byte[] { 0x0C, 0x09, 0x57, 0x75, 0x6E, 0x64, 0x65, 0x72, 0x62, 0x61,
            0x72, 0x49, 0x52, 0x03, 0x19, 0x00, 0x02, 0x02, 0x01, 0x06, 0x07, 0x03, 0x00, 0x20,
            0x0A, 0x18, 0x0F, 0x18 };


    @Test public void getServiceUuid_shouldContain3Services() {
        List<String> services = AdvertisementPacketParser.decodeServicesUuid(data);
        Assert.assertEquals(3, services.size());
    }

    @Test public void getServiceUuid_shouldContainSpecificServices() {
        List<String> services = AdvertisementPacketParser.decodeServicesUuid(data);
        Assert.assertTrue(services.contains("2000"));
        Assert.assertTrue(services.contains("180a"));
        Assert.assertTrue(services.contains("180f"));
    }

    @Test public void decodeDeviceName_ShouldBeWunderbarIR() {
        Assert.assertEquals("WunderbarIR", AdvertisementPacketParser.decodeDeviceName(data));
    }

}
