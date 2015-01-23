package io.relayr.model;

import java.io.Serializable;
import java.util.List;

import io.relayr.RelayrSdk;
import rx.Observable;

/** The Transmitter class is a representation of the Transmitter entity.
 * A Transmitter is another basic entity on the relayr platform.
 * A transmitter contrary to a device does not gather data but is only used to relay the data
 * from the devices to the relayr platform.
 * The transmitter is also used to authenticate the different devices that transmit data via it. */
public class Transmitter implements Serializable {

    public final String id;
    public final String secret;
    public final String owner;
    private String name;

    public Transmitter(String id, String secret, String owner, String name) {
        this.id = id;
        this.secret = secret;
        this.owner = owner;
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /**
     * @return an {@link rx.Observable} with a list of devices that belong to the specific
     * transmitter. */
    public Observable<List<TransmitterDevice>> getDevices() {
        return RelayrSdk.getRelayrApi().getTransmitterDevices(id);
    }

    /** Updates a transmitter.
     * @return an {@link rx.Observable} to the updated Transmitter */
    public Observable<Transmitter> updateTransmitter() {
        return RelayrSdk.getRelayrApi().updateTransmitter(this, id);
    }

}
