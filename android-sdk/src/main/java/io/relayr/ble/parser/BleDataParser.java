package io.relayr.ble.parser;

import com.google.gson.Gson;

import io.relayr.ble.BleDeviceType;
import io.relayr.model.AccelGyroscope;
import io.relayr.model.DeviceModel;
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
        reading.modelId = DeviceModel.LIGHT_PROX_COLOR.getId();
        reading.received = System.currentTimeMillis();

        int red = (byteToUnsignedInt(value[3]) << 8) | byteToUnsignedInt(value[2]);
        int green = (byteToUnsignedInt(value[5]) << 8) | byteToUnsignedInt(value[4]);
        int blue = (byteToUnsignedInt(value[7]) << 8) | byteToUnsignedInt(value[6]);
        LightColorProx.Color color = new LightColorProx.Color(red, green, blue);
        reading.readings.add(new Reading.Data(reading.received, "color", "", color));

        int proximity = (byteToUnsignedInt(value[9]) << 8) | byteToUnsignedInt(value[8]);
        reading.readings.add(new Reading.Data(reading.received, "proximity", "", proximity));
        
        int luminosity = (byteToUnsignedInt(value[1]) << 8) | byteToUnsignedInt(value[0]);
        reading.readings.add(new Reading.Data(reading.received, "luminosity", "", luminosity));
        
        return new Gson().toJson(reading);
    }

    private static String getGYROSensorData(byte[] value) {
        Reading reading = new Reading();
        reading.modelId = DeviceModel.ACCELEROMETER_GYROSCOPE.getId();
        reading.received = System.currentTimeMillis();
        
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

        AccelGyroscope.Acceleration acceleration = new AccelGyroscope.Acceleration();
        acceleration.x = (float) accelerationX / 100.0f;
        acceleration.y = (float) accelerationY / 100.0f;
        acceleration.z = (float) accelerationZ / 100.0f;
        reading.readings.add(new Reading.Data(reading.received, "acceleration", "", acceleration));

        AccelGyroscope.AngularSpeed angularSpeed = new AccelGyroscope.AngularSpeed();
        angularSpeed.x = (float) gyroscopeX / 100.0f;
        angularSpeed.y = (float) gyroscopeY / 100.0f;
        angularSpeed.z = (float) gyroscopeZ / 100.0f;
        reading.readings.add(new Reading.Data(reading.received, "angularSpeed", "", angularSpeed));
        
        return new Gson().toJson(reading);
    }

    private static String getHTUSensorData(byte[] value) {
        Reading reading = new Reading();
        reading.modelId = DeviceModel.TEMPERATURE_HUMIDITY.getId();
        reading.received = System.currentTimeMillis();

        int temperature = (byteToUnsignedInt(value[1]) << 8) | byteToUnsignedInt(value[0]);
        int humidity = (byteToUnsignedInt(value[3]) << 8) | byteToUnsignedInt(value[2]);

        reading.readings.add(new Reading.Data(reading.received, "humidity", "", (int) ((float) humidity / 100.0f)));
        reading.readings.add(new Reading.Data(reading.received, "temperature", "", (float) temperature / 100.0f));
        return new Gson().toJson(reading);
    }

    private static String getMICSensorData(byte[] value) {
        Reading reading = new Reading();
        reading.modelId = DeviceModel.MICROPHONE.getId();
        reading.received = System.currentTimeMillis();

        int noiseLevel = (byteToUnsignedInt(value[1]) << 8) | byteToUnsignedInt(value[0]);

        reading.readings.add(new Reading.Data(reading.received, "noiseLevel", "", noiseLevel));
        return new Gson().toJson(reading);
    }

    private static int byteToUnsignedInt(byte b) {
        return (int) b & 0xff;
    }
}
