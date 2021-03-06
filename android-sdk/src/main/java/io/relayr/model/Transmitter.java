package io.relayr.model;

import java.io.Serializable;
import java.util.List;

import io.relayr.RelayrSdk;
import rx.Observable;

/**
 * The Transmitter class is a representation of the Transmitter entity.
 * A Transmitter is another basic entity on the relayr platform.
 * A transmitter contrary to a device does not gather data but is only used to relay the data
 * from the devices to the relayr platform.
 * The transmitter is also used to authenticate the different devices that transmit data via it.
 */
public class Transmitter implements Serializable {

    public String id;
    public String secret;
    public String owner;
    private String topic;
    private String integrationType;
    private String name;
    private String clientId;
    private MqttChannel.MqttCredentials credentials;

    public Transmitter(String owner, String name, IntegrationType type) {
        this.owner = owner;
        this.name = name;
        this.integrationType = type.getName();
    }

    public Transmitter(String id, String secret, String owner, String name) {
        this.id = id;
        this.secret = secret;
        this.owner = owner;
        this.name = name;
        setIntegrationType(IntegrationType.WUNDERBAR_1);
    }

    /**
     * @return an {@link rx.Observable} with a list of devices that belong to the specific
     * transmitter.
     */
    public Observable<List<TransmitterDevice>> getDevices() {
        return RelayrSdk.getRelayrApi().getTransmitterDevices(id);
    }

    /**
     * Updates a transmitter.
     * @return an {@link rx.Observable} to the updated Transmitter
     */
    public Observable<Transmitter> updateTransmitter() {
        return RelayrSdk.getRelayrApi().updateTransmitter(this, id);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getTopic() {
        return topic;
    }

    public IntegrationType getIntegrationType() {
        return IntegrationType.getByName(integrationType);
    }

    public void setIntegrationType(IntegrationType integrationType) {
        this.integrationType = integrationType.getName();
    }

    public String getClientId() {
        return clientId;
    }

    public MqttChannel.MqttCredentials getCredentials() {
        return credentials;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    @Override public String toString() {
        return "Transmitter{" +
                "id='" + id + '\'' +
                ", secret='" + secret + '\'' +
                ", owner='" + owner + '\'' +
                ", topic='" + topic + '\'' +
                ", integrationType='" + integrationType + '\'' +
                ", name='" + name + '\'' +
                ", clientId='" + clientId + '\'' +
                ", credentials=" + credentials +
                '}';
    }

    public void setCredentials(MqttChannel.MqttCredentials credentials) {
        this.credentials = credentials;
    }
}
