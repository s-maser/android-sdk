package io.relayr.model;

public class FirmwareVersion {

    final public String version;
    final public Object configuration;

    public FirmwareVersion(String version, Object configuration) {
        this.version = version;
        this.configuration = configuration;
    }

    @Override
    public String toString() {
        return "FirmwareVersion{" +
                "version='" + version + '\'' +
                ", configuration=" + configuration +
                '}';
    }
}
