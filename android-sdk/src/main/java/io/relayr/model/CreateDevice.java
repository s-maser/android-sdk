package io.relayr.model;

import java.io.Serializable;

public class CreateDevice implements Serializable {

    private String mac;
    private String name;
    private String model;
    private String owner;
    private String firmwareVersion;
    private String integrationType;
    private String transmitterId;

    public CreateDevice(String name, DeviceModel model, String owner, String mac, String transmitterId) {
        this.name = name;
        this.model = model.getId();
        this.owner = owner;
        this.mac = mac;
        this.transmitterId = transmitterId;
        this.integrationType = IntegrationType.WUNDERBAR_2.getName();
        this.firmwareVersion = "1.0.0";
    }

    public String getMac() {
        return mac;
    }

    public String getName() {
        return name;
    }
}
