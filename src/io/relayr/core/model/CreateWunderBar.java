package io.relayr.core.model;

import java.io.Serializable;

public class CreateWunderBar implements Serializable {

    public final Transmitter masterModule;
    public final Device gyroscope;
    public final Device light;
    public final Device microphone;
    public final Device thermometer;
    public final Device infrared;
    public final Device bridge;

    public CreateWunderBar(Transmitter masterModule, Device gyroscope,
                           Device light, Device microphone,
                           Device thermometer, Device infrared,
                           Device bridge) {
        this.masterModule = masterModule;
        this.gyroscope = gyroscope;
        this.light = light;
        this.microphone = microphone;
        this.thermometer = thermometer;
        this.infrared = infrared;
        this.bridge = bridge;
    }

    public WunderBar toWunderBar() {
        return new WunderBar(masterModule, toTransmitterDevice(gyroscope),
                toTransmitterDevice(light), toTransmitterDevice(microphone),
                toTransmitterDevice(thermometer), toTransmitterDevice(infrared),
                toTransmitterDevice(bridge));
    }

    private TransmitterDevice toTransmitterDevice(Device device) {
        return new TransmitterDevice(device.id, device.getSecret(), device.getOwner(),
                device.getName(), device.getModel().getId());
    }

}
