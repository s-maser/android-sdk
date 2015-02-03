package io.relayr.model;

import java.io.Serializable;

public class MqttDefinition implements Serializable{
   
    private String deviceId;
    private String transport;

    public MqttDefinition(String deviceId, String transport) {
        this.deviceId = deviceId;
        this.transport = transport;
    }
}
