package io.relayr.model;

/**
 * A reading is the information gathered by the device.
 */
public class Reading {

    public final long received;
    public final long recorded;
    public final String meaning;
    public final String path;
    public final Object value;

    public Reading(long received, long recorded, String meaning, String path, Object value) {
        this.received = received;
        this.recorded = recorded;
        this.meaning = meaning;
        this.path = path;
        this.value = value;
    }

}
