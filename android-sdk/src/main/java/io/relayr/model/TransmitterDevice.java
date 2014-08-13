package io.relayr.model;

public class TransmitterDevice extends Transmitter {

    public final String model;

    public TransmitterDevice(String id, String secret, String owner, String name,
                             String model) {
        super(id, secret, owner, name);
        this.model = model;
    }
}
