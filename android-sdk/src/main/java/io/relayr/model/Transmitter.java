package io.relayr.model;

import java.io.Serializable;

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
}
