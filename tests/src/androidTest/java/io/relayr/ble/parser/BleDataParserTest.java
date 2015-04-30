package io.relayr.ble.parser;

import com.google.gson.Gson;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import io.relayr.model.AccelGyroscope;
import io.relayr.model.DataPackage;
import io.relayr.model.LightColorProx;

import static io.relayr.ble.BleDeviceType.WunderbarGYRO;
import static io.relayr.ble.BleDeviceType.WunderbarHTU;
import static io.relayr.ble.BleDeviceType.WunderbarLIGHT;
import static io.relayr.ble.BleDeviceType.WunderbarMIC;

@RunWith(RobolectricTestRunner.class)
public class BleDataParserTest {

    private byte[] lightData = new byte[]{
            (byte) 0xFD, 0x09, 0x23, 0x06, 0x18, 0x05, 0x16, 0x00, (byte) 0xFF, 0x07};
    private byte[] gyroData = new byte[]{0x32, (byte) 0xFE, (byte) 0xFF, (byte) 0xFF, (byte) 0xD0,
            0x04, 0x00, 0x00, (byte) 0xE7, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, 0x0F, 0x00, 0x01,
            0x00, 0x5F, 0x00};

    @Test
    public void getFormattedValue_for_WunderbarLIGHT_lightValue() {
        String val = BleDataParser.getFormattedValue(WunderbarLIGHT, lightData);
        DataPackage dataPackage = new Gson().fromJson(val, DataPackage.class);
        int light = new Gson().fromJson(dataPackage.readings.get(2).value.toString(), Integer.class);
        Assert.assertEquals(22, light, 5);
    }

    @Test
    public void getFormattedValue_for_WunderbarLIGHT_redColor() {
        String val = BleDataParser.getFormattedValue(WunderbarLIGHT, lightData);
        DataPackage dataPackage = new Gson().fromJson(val, DataPackage.class);
        LightColorProx.Color color = new Gson().fromJson(dataPackage.readings.get(0).value.toString(), LightColorProx.Color.class);
        Assert.assertEquals(2557, color.red);
    }

    @Test
    public void getFormattedValue_for_WunderbarLIGHT_blueColor() {
        String val = BleDataParser.getFormattedValue(WunderbarLIGHT, lightData);
        DataPackage dataPackage = new Gson().fromJson(val, DataPackage.class);
        LightColorProx.Color color = new Gson().fromJson(dataPackage.readings.get(0).value.toString(), LightColorProx.Color.class);
        Assert.assertEquals(1304, color.blue);
    }

    @Test
    public void getFormattedValue_for_WunderbarLIGHT_greenColor() {
        String val = BleDataParser.getFormattedValue(WunderbarLIGHT, lightData);
        DataPackage dataPackage = new Gson().fromJson(val, DataPackage.class);
        LightColorProx.Color color = new Gson().fromJson(dataPackage.readings.get(0).value.toString(), LightColorProx.Color.class);
        Assert.assertEquals(1571, color.green);
    }

    @Test
    public void getFormattedValue_for_WunderbarLIGHT_proximityValue() {
        String val = BleDataParser.getFormattedValue(WunderbarLIGHT, lightData);
        DataPackage dataPackage = new Gson().fromJson(val, DataPackage.class);
        int proximity = new Gson().fromJson(dataPackage.readings.get(1).value.toString(), Integer.class);
        Assert.assertEquals(2047, proximity, 5);
    }

    @Test
    public void getFormattedValue_for_WunderbarGyro_xAngularSpeed() {
        String val = BleDataParser.getFormattedValue(WunderbarGYRO, gyroData);
        DataPackage dataPackage = new Gson().fromJson(val, DataPackage.class);
        AccelGyroscope.AngularSpeed angularSpeed = new Gson().fromJson(
                dataPackage.readings.get(1).value.toString(), AccelGyroscope.AngularSpeed.class);
        Assert.assertEquals(-4.62, angularSpeed.x, 5);
    }

    @Test
    public void getFormattedValue_for_WunderbarGyro_yAngularSpeed() {
        String val = BleDataParser.getFormattedValue(WunderbarGYRO, gyroData);
        DataPackage dataPackage = new Gson().fromJson(val, DataPackage.class);
        AccelGyroscope.AngularSpeed angularSpeed = new Gson().fromJson(
                dataPackage.readings.get(1).value.toString(), AccelGyroscope.AngularSpeed.class);
        Assert.assertEquals(12.32, angularSpeed.y, 5);
    }

    @Test
    public void getFormattedValue_for_WunderbarGyro_zAngularSpeed() {
        String val = BleDataParser.getFormattedValue(WunderbarGYRO, gyroData);
        DataPackage dataPackage = new Gson().fromJson(val, DataPackage.class);
        AccelGyroscope.AngularSpeed angularSpeed = new Gson().fromJson(
                dataPackage.readings.get(1).value.toString(), AccelGyroscope.AngularSpeed.class);
        Assert.assertEquals(-0.25, angularSpeed.z, 5);
    }

    @Test
    public void getFormattedValue_for_WunderbarGyro_xAcceleration() {
        String val = BleDataParser.getFormattedValue(WunderbarGYRO, gyroData);
        DataPackage dataPackage = new Gson().fromJson(val, DataPackage.class);
        AccelGyroscope.Acceleration acceleration = new Gson().fromJson(
                dataPackage.readings.get(0).value.toString(), AccelGyroscope.Acceleration.class);
        Assert.assertEquals(0.15, acceleration.x, 5);
    }

    @Test
    public void getFormattedValue_for_WunderbarGyro_yAcceleration() {
        String val = BleDataParser.getFormattedValue(WunderbarGYRO, gyroData);
        DataPackage dataPackage = new Gson().fromJson(val, DataPackage.class);
        AccelGyroscope.Acceleration acceleration = new Gson().fromJson(
                dataPackage.readings.get(0).value.toString(), AccelGyroscope.Acceleration.class);
        Assert.assertEquals(0.01, acceleration.y, 5);
    }

    @Test
    public void getFormattedValue_for_WunderbarGyro_zAcceleration() {
        String val = BleDataParser.getFormattedValue(WunderbarGYRO, gyroData);
        DataPackage dataPackage = new Gson().fromJson(val, DataPackage.class);
        AccelGyroscope.Acceleration acceleration = new Gson().fromJson(
                dataPackage.readings.get(0).value.toString(), AccelGyroscope.Acceleration.class);
        Assert.assertEquals(0.95, acceleration.z, 5);
    }

    @Test
    public void getFormattedValue_for_WunderbarMIC() {
        byte[] data = new byte[]{0x59, 0x00};
        String val = BleDataParser.getFormattedValue(WunderbarMIC, data);
        DataPackage dataPackage = new Gson().fromJson(val, DataPackage.class);
        int noise = new Gson().fromJson(dataPackage.readings.get(0).value.toString(), Integer.class);
        Assert.assertEquals(89, noise, 5);
    }

    @Test
    public void getFormattedValue_for_WunderbarHTU_temperatureValue() {
        byte[] data = new byte[]{(byte) 0xA6, 0x09, 0x79, 0x15};
        String val = BleDataParser.getFormattedValue(WunderbarHTU, data);
        DataPackage dataPackage = new Gson().fromJson(val, DataPackage.class);
        double temperature = new Gson().fromJson(dataPackage.readings.get(1).value.toString(), Double.class);
        Assert.assertEquals(24.7, temperature, 5);
    }

    @Test
    public void getFormattedValue_for_WunderbarHTU_humidityValue() {
        byte[] data = new byte[]{(byte) 0xA6, 0x09, 0x79, 0x15};
        String val = BleDataParser.getFormattedValue(WunderbarHTU, data);
        DataPackage dataPackage = new Gson().fromJson(val, DataPackage.class);
        int humidity = new Gson().fromJson(dataPackage.readings.get(0).value.toString(), Integer.class);
        Assert.assertEquals(55, humidity, 5);
    }

}

