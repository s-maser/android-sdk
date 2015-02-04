package io.relayr.ble.parser;

import com.google.gson.Gson;

import io.relayr.ble.BleDeviceType;
import io.relayr.model.AccelGyroscope;
import io.relayr.model.LightColorProx;
import io.relayr.model.Reading;

public abstract class BleDataParser {

    public static String getFormattedValue(BleDeviceType type, byte[] value) {
        if (value == null) return "";
        switch (type) {
            case WunderbarLIGHT: {
                return BleDataParser.getLIGHTSensorData(value);
            }
            case WunderbarGYRO: {
                return BleDataParser.getGYROSensorData(value);
            }
            case WunderbarHTU: {
                return BleDataParser.getHTUSensorData(value);
            }
            case WunderbarMIC: {
                return BleDataParser.getMICSensorData(value);
            }
            default: return "";
        }
    }

    private static String getLIGHTSensorData(byte[] value) {
        Reading reading = new Reading();
        reading.readings.color = new LightColorProx.Color();
        reading.readings.luminosity = (byteToUnsignedInt(value[1]) << 8) | byteToUnsignedInt(value[0]);
        reading.readings.color.red = (byteToUnsignedInt(value[3]) << 8) | byteToUnsignedInt(value[2]);
        reading.readings.color.green = (byteToUnsignedInt(value[5]) << 8) | byteToUnsignedInt(value[4]);
        reading.readings.color.blue = (byteToUnsignedInt(value[7]) << 8) | byteToUnsignedInt(value[6]);
        reading.readings.proximity = (byteToUnsignedInt(value[9]) << 8) | byteToUnsignedInt(value[8]);
        return new Gson().toJson(reading);
    }

    private static String getGYROSensorData(byte[] value) {
        Reading reading = new Reading();
        int gyroscopeX = byteToUnsignedInt(value[0]) |
                (byteToUnsignedInt(value[1]) << 8) |
                (byteToUnsignedInt(value[2]) << 16) |
                (byteToUnsignedInt(value[3]) << 24);
        int gyroscopeY = byteToUnsignedInt(value[4]) |
                (byteToUnsignedInt(value[5]) << 8) |
                (byteToUnsignedInt(value[6]) << 16) |
                (byteToUnsignedInt(value[7]) << 24);
        int gyroscopeZ = byteToUnsignedInt(value[8]) |
                (byteToUnsignedInt(value[9]) << 8) |
                (byteToUnsignedInt(value[10]) << 16) |
                (byteToUnsignedInt(value[11]) << 24);

        int accelerationX = (byteToUnsignedInt(value[13]) << 8) | byteToUnsignedInt(value[12]);
        int accelerationY = (byteToUnsignedInt(value[15]) << 8) | byteToUnsignedInt(value[14]);
        int accelerationZ = (byteToUnsignedInt(value[17]) << 8) | byteToUnsignedInt(value[16]);

        reading.readings.acceleration = new AccelGyroscope.Accelerometer();
        reading.readings.acceleration.x = (float) accelerationX / 100.0f;
        reading.readings.acceleration.y = (float) accelerationY / 100.0f;
        reading.readings.acceleration.z = (float) accelerationZ / 100.0f;

        reading.readings.angularSpeed = new AccelGyroscope.Gyroscope();
        reading.readings.angularSpeed.x = (float) gyroscopeX / 100.0f;
        reading.readings.angularSpeed.y = (float) gyroscopeY / 100.0f;
        reading.readings.angularSpeed.z = (float) gyroscopeZ / 100.0f;
        return new Gson().toJson(reading);
    }

    private static String getHTUSensorData(byte[] value) {
        Reading reading = new Reading();
        int temperature = (byteToUnsignedInt(value[1]) << 8) | byteToUnsignedInt(value[0]);
        int humidity = (byteToUnsignedInt(value[3]) << 8) | byteToUnsignedInt(value[2]);

        reading.readings.humidity = (int) ((float) humidity / 100.0f);
        reading.readings.temperature = (float) temperature / 100.0f;
        return new Gson().toJson(reading);
    }

    private static String getMICSensorData(byte[] value) {
        Reading reading = new Reading();
        reading.readings.noiseLevel = (byteToUnsignedInt(value[1]) << 8) | byteToUnsignedInt(value[0]);
        return new Gson().toJson(reading);
    }

    private static int byteToUnsignedInt(byte b) {
        return (int) b & 0xff;
    }
}
