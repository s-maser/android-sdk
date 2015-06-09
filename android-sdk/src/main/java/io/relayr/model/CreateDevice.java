package io.relayr.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CreateDevice implements Serializable {

    private String mac;
    private String name;
    private String model;
    private String owner;
    private String firmwareVersion;
    @SerializedName("integrationType") private String accountType;
    private String transmitterId;

    public CreateDevice(String name, DeviceModel model, String owner, String mac, String transmitterId) {
        this.name = name;
        this.model = model.getId();
        this.owner = owner;
        this.mac = mac;
        this.transmitterId = transmitterId;
        this.accountType = AccountType.WUNDERBAR_2.getName();
        this.firmwareVersion = "2.0.0";
    }

    public String getMac() {
        return mac;
    }

    public String getName() {
        return name;
    }
}
