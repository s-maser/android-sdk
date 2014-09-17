package io.relayr.ble.parser;

import com.google.gson.Gson;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import io.relayr.model.Reading;

import static io.relayr.ble.BleDeviceType.WunderbarGYRO;
import static io.relayr.ble.BleDeviceType.WunderbarHTU;
import static io.relayr.ble.BleDeviceType.WunderbarLIGHT;
import static io.relayr.ble.BleDeviceType.WunderbarMIC;

@RunWith(RobolectricTestRunner.class)
public class BleDataParserTest {

    private byte[] lightData = new byte[] {
                (byte) 0xFD, 0x09, 0x23, 0x06, 0x18, 0x05, 0x16, 0x00, (byte) 0xFF, 0x07 };
    private byte[] gyroData = new byte[] { 0x32, (byte) 0xFE, (byte) 0xFF, (byte) 0xFF, (byte) 0xD0,
            0x04, 0x00, 0x00, (byte) 0xE7, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, 0x0F, 0x00, 0x01,
            0x00, 0x5F, 0x00 };

    @Test public void getFormattedValue_for_WunderbarLIGHT_lightValue() {
        String val = BleDataParser.getFormattedValue(WunderbarLIGHT, lightData);
        Reading reading = new Gson().fromJson(val, Reading.class);
        Assert.assertEquals(2557, reading.light, 5);
    }

    @Test public void getFormattedValue_for_WunderbarLIGHT_redColor() {
        String val = BleDataParser.getFormattedValue(WunderbarLIGHT, lightData);
        Reading reading = new Gson().fromJson(val, Reading.class);
        Assert.assertEquals(1571, reading.clr.r);
    }

    @Test public void getFormattedValue_for_WunderbarLIGHT_blueColor() {
        String val = BleDataParser.getFormattedValue(WunderbarLIGHT, lightData);
        Reading reading = new Gson().fromJson(val, Reading.class);
        Assert.assertEquals(22, reading.clr.b);
    }

    @Test public void getFormattedValue_for_WunderbarLIGHT_greenColor() {
        String val = BleDataParser.getFormattedValue(WunderbarLIGHT, lightData);
        Reading reading = new Gson().fromJson(val, Reading.class);
        Assert.assertEquals(1304, reading.clr.g);
    }

    @Test public void getFormattedValue_for_WunderbarLIGHT_proximityValue() {
        String val = BleDataParser.getFormattedValue(WunderbarLIGHT, lightData);
        Reading reading = new Gson().fromJson(val, Reading.class);
        Assert.assertEquals(2047, reading.prox, 5);
    }

    @Test public void getFormattedValue_for_WunderbarGyro_xGyroscopeValue() {
        String val = BleDataParser.getFormattedValue(WunderbarGYRO, gyroData);
        Reading reading = new Gson().fromJson(val, Reading.class);
        Assert.assertEquals(-4.62, reading.gyro.x, 5);
    }

    @Test public void getFormattedValue_for_WunderbarGyro_yGyroscopeValue() {
        String val = BleDataParser.getFormattedValue(WunderbarGYRO, gyroData);
        Reading reading = new Gson().fromJson(val, Reading.class);
        Assert.assertEquals(12.32, reading.gyro.y, 5);
    }

    @Test public void getFormattedValue_for_WunderbarGyro_zGyroscopeValue() {
        String val = BleDataParser.getFormattedValue(WunderbarGYRO, gyroData);
        Reading reading = new Gson().fromJson(val, Reading.class);
        Assert.assertEquals(-0.25, reading.gyro.z, 5);
    }

    @Test public void getFormattedValue_for_WunderbarGyro_xAccelerometerValue() {
        String val = BleDataParser.getFormattedValue(WunderbarGYRO, gyroData);
        Reading reading = new Gson().fromJson(val, Reading.class);
        Assert.assertEquals(0.15, reading.accel.x, 5);
    }

    @Test public void getFormattedValue_for_WunderbarGyro_yAccelerometerValue() {
        String val = BleDataParser.getFormattedValue(WunderbarGYRO, gyroData);
        Reading reading = new Gson().fromJson(val, Reading.class);
        Assert.assertEquals(0.01, reading.accel.y, 5);
    }

    @Test public void getFormattedValue_for_WunderbarGyro_zAccelerometerValue() {
        String val = BleDataParser.getFormattedValue(WunderbarGYRO, gyroData);
        Reading reading = new Gson().fromJson(val, Reading.class);
        Assert.assertEquals(0.95, reading.accel.z, 5);
    }

    @Test public void getFormattedValue_for_WunderbarMIC() {
        byte[] data = new byte[] { 0x59, 0x00 };
        String val = BleDataParser.getFormattedValue(WunderbarMIC, data);
        Reading reading = new Gson().fromJson(val, Reading.class);
        Assert.assertEquals(89, reading.snd_level, 5);
    }

    @Test public void getFormattedValue_for_WunderbarHTU_temperatureValue() {
        byte[] data = new byte[] { (byte) 0xA6, 0x09, 0x79, 0x15 };
        String val = BleDataParser.getFormattedValue(WunderbarHTU, data);
        Reading reading = new Gson().fromJson(val, Reading.class);
        Assert.assertEquals(24.70000, reading.temp, 5);
    }

    @Test public void getFormattedValue_for_WunderbarHTU_humidityValue() {
        byte[] data = new byte[] { (byte) 0xA6, 0x09, 0x79, 0x15 };
        String val = BleDataParser.getFormattedValue(WunderbarHTU, data);
        Reading reading = new Gson().fromJson(val, Reading.class);
        Assert.assertEquals(54.97000, reading.hum, 5);
    }

}

