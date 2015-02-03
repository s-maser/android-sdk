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
        Assert.assertEquals(2557, reading.readings.luminosity, 5);
    }

    @Test public void getFormattedValue_for_WunderbarLIGHT_redColor() {
        String val = BleDataParser.getFormattedValue(WunderbarLIGHT, lightData);
        Reading reading = new Gson().fromJson(val, Reading.class);
        Assert.assertEquals(1571, reading.readings.color.red);
    }

    @Test public void getFormattedValue_for_WunderbarLIGHT_blueColor() {
        String val = BleDataParser.getFormattedValue(WunderbarLIGHT, lightData);
        Reading reading = new Gson().fromJson(val, Reading.class);
        Assert.assertEquals(22, reading.readings.color.blue);
    }

    @Test public void getFormattedValue_for_WunderbarLIGHT_greenColor() {
        String val = BleDataParser.getFormattedValue(WunderbarLIGHT, lightData);
        Reading reading = new Gson().fromJson(val, Reading.class);
        Assert.assertEquals(1304, reading.readings.color.green);
    }

    @Test public void getFormattedValue_for_WunderbarLIGHT_proximityValue() {
        String val = BleDataParser.getFormattedValue(WunderbarLIGHT, lightData);
        Reading reading = new Gson().fromJson(val, Reading.class);
        Assert.assertEquals(2047, reading.readings.proximity, 5);
    }

    @Test public void getFormattedValue_for_WunderbarGyro_xGyroscopeValue() {
        String val = BleDataParser.getFormattedValue(WunderbarGYRO, gyroData);
        Reading reading = new Gson().fromJson(val, Reading.class);
        Assert.assertEquals(-4.62, reading.readings.angularSpeed.x, 5);
    }

    @Test public void getFormattedValue_for_WunderbarGyro_yGyroscopeValue() {
        String val = BleDataParser.getFormattedValue(WunderbarGYRO, gyroData);
        Reading reading = new Gson().fromJson(val, Reading.class);
        Assert.assertEquals(12.32, reading.readings.angularSpeed.y, 5);
    }

    @Test public void getFormattedValue_for_WunderbarGyro_zGyroscopeValue() {
        String val = BleDataParser.getFormattedValue(WunderbarGYRO, gyroData);
        Reading reading = new Gson().fromJson(val, Reading.class);
        Assert.assertEquals(-0.25, reading.readings.angularSpeed.z, 5);
    }

    @Test public void getFormattedValue_for_WunderbarGyro_xAccelerometerValue() {
        String val = BleDataParser.getFormattedValue(WunderbarGYRO, gyroData);
        Reading reading = new Gson().fromJson(val, Reading.class);
        Assert.assertEquals(0.15, reading.readings.acceleration.x, 5);
    }

    @Test public void getFormattedValue_for_WunderbarGyro_yAccelerometerValue() {
        String val = BleDataParser.getFormattedValue(WunderbarGYRO, gyroData);
        Reading reading = new Gson().fromJson(val, Reading.class);
        Assert.assertEquals(0.01, reading.readings.acceleration.y, 5);
    }

    @Test public void getFormattedValue_for_WunderbarGyro_zAccelerometerValue() {
        String val = BleDataParser.getFormattedValue(WunderbarGYRO, gyroData);
        Reading reading = new Gson().fromJson(val, Reading.class);
        Assert.assertEquals(0.95, reading.readings.acceleration.z, 5);
    }

    @Test public void getFormattedValue_for_WunderbarMIC() {
        byte[] data = new byte[] { 0x59, 0x00 };
        String val = BleDataParser.getFormattedValue(WunderbarMIC, data);
        Reading reading = new Gson().fromJson(val, Reading.class);
        Assert.assertEquals(89, reading.readings.noiseLevel, 5);
    }

    @Test public void getFormattedValue_for_WunderbarHTU_temperatureValue() {
        byte[] data = new byte[] { (byte) 0xA6, 0x09, 0x79, 0x15 };
        String val = BleDataParser.getFormattedValue(WunderbarHTU, data);
        Reading reading = new Gson().fromJson(val, Reading.class);
        Assert.assertEquals(24.70000, reading.readings.temperature, 5);
    }

    @Test public void getFormattedValue_for_WunderbarHTU_humidityValue() {
        byte[] data = new byte[] { (byte) 0xA6, 0x09, 0x79, 0x15 };
        String val = BleDataParser.getFormattedValue(WunderbarHTU, data);
        Reading reading = new Gson().fromJson(val, Reading.class);
        Assert.assertEquals(54.97000, reading.readings.humidity, 5);
    }

}

