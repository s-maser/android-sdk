package io.relayr.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.relayr.RelayrSdk;
import io.relayr.ble.BleDevicesCache;
import io.relayr.ble.service.BaseService;
import rx.Observable;

/**
 * The Device class is a representation of the device entity.
 * A device entity is any external entity capable of gathering measurements
 * or one which is capable of receiving information from the relayr platform.
 * Examples would be a thermometer, a gyroscope or an infrared sensor.
 */
public class Device implements Serializable {

    /** Auto generated uid */
    private static final long serialVersionUID = 1L;
    public final String id;
    private String name;
    private final Model model;
    private String owner;
    private String firmwareVersion;
    private final String secret;
    private String integrationType;
    private String externalId;
    @SerializedName("public") private boolean isPublic;

    public Device(String id, String name, Model model, String owner,
                  String firmwareVersion, String secret, boolean isPublic) {
        this.id = id;
        this.name = name;
        this.model = model;
        this.owner = owner;
        this.firmwareVersion = firmwareVersion;
        this.secret = secret;
        this.isPublic = isPublic;

        setIntegrationType(IntegrationType.WUNDERBAR_1);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Model getModel() {
        return model;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getSecret() {
        return secret;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    public IntegrationType getIntegrationType() {
        return IntegrationType.getByName(integrationType);
    }

    public void setIntegrationType(IntegrationType integrationType) {
        this.integrationType = integrationType.getName();
    }

    @Override
    public String toString() {
        return "Relayr_Device{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", model=" + model +
                ", owner='" + owner + '\'' +
                ", firmwareVersion='" + firmwareVersion + '\'' +
                ", secret='" + secret + '\'' +
                ", isPublic=" + isPublic +
                '}';
    }

    public TransmitterDevice toTransmitterDevice() {
        return new TransmitterDevice(id, secret, owner, name, model.getId());
    }

    public Observable<BaseService> getSensorForDevice(BleDevicesCache cache) {
        return cache.getSensorForDevice(toTransmitterDevice());
    }

    /**
     * Subscribes an app to a BLE device. Enables the app to receive data from the device over
     * BLE through {@link io.relayr.ble.service.DirectConnectionService}
     */
    public Observable<Reading> subscribeToBleReadings(final BleDevicesCache cache) {
        return toTransmitterDevice().subscribeToBleReadings(cache);
    }

    /**
     * Subscribes an app to a device channel. Enables the app to receive data from the device.
     */
    public Observable<Reading> subscribeToCloudReadings() {
        return RelayrSdk.getWebSocketClient().subscribe(toTransmitterDevice());
    }

    /**
     * Unsubscribes an app from a device channel, stopping and cleaning up the connection.
     */

    public void unSubscribeToCloudReadings() {
        RelayrSdk.getWebSocketClient().unSubscribe(id);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof TransmitterDevice && ((TransmitterDevice) o).id.equals(id) ||
                o instanceof Device && ((Device) o).id.equals(id);
    }

    /** Sends a command to the this device */
    public Observable<Void> sendCommand(Command command) {
        return RelayrSdk.getRelayrApi().sendCommand(id, command);
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }
}
