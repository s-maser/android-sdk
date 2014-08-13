package io.relayr.core.model;

import java.io.Serializable;
import java.util.List;

public class WunderBar implements Serializable {

    public final Transmitter masterModule;
    public final TransmitterDevice gyroscope;
    public final TransmitterDevice light;
    public final TransmitterDevice microphone;
    public final TransmitterDevice thermometer;
    public final TransmitterDevice infrared;
    public final TransmitterDevice bridge;

    public WunderBar(Transmitter masterModule, TransmitterDevice gyroscope,
                     TransmitterDevice light, TransmitterDevice microphone,
                     TransmitterDevice thermometer, TransmitterDevice infrared,
                     TransmitterDevice bridge) {
        this.masterModule = masterModule;
        this.gyroscope = gyroscope;
        this.light = light;
        this.microphone = microphone;
        this.thermometer = thermometer;
        this.infrared = infrared;
        this.bridge = bridge;
    }

    public static WunderBar from(Transmitter masterModule, List<TransmitterDevice> devices) {
        TransmitterDevice gyroscope = null, light = null, microphone = null,
                thermometer = null, infrared = null, bridge = null;
        for (TransmitterDevice device : devices) {
            if (DeviceModel.ACCELEROMETER_GYROSCOPE.getId().equals(device.model)) {
                gyroscope = device;
            } else if (DeviceModel.LIGHT_PROX_COLOR.getId().equals(device.model)) {
                light = device;
            } else if (DeviceModel.MICROPHONE.getId().equals(device.model)) {
                microphone = device;
            } else if (DeviceModel.TEMPERATURE_HUMIDITY.getId().equals(device.model)) {
                thermometer = device;
            } else if (DeviceModel.IR_TRANSMITTER.getId().equals(device.model)) {
                infrared = device;
            } else if (DeviceModel.GROVE.getId().equals(device.model)) {
                bridge = device;
            }
        }
        return new WunderBar(masterModule, gyroscope, light, microphone, thermometer, infrared,
                bridge);
    }

}
