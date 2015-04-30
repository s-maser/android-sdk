package io.relayr.model;

import java.io.Serializable;
import java.util.List;

/**
 * A model is an entity which holds the following information about a device:
 * Name
 * Manufacturer
 * List of readings provided along with the minimum and maximum values and the units in which these
 * are measured.
 */
public class Model implements Serializable {

    /** Auto generated uid */
    private static final long serialVersionUID = 1L;
    private String id;
    private String name;
    private String manufacturer;
    private List<ModelReading> readings;
    private List<ModelCommand> commands;
    private List<FirmwareVersion> firmwareVersions;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public List<ModelReading> getReadings() {
        return readings;
    }

    public void setReadings(List<ModelReading> readings) {
        this.readings = readings;
    }

    public List<ModelCommand> getCommands() {
        return commands;
    }

    public void setCommands(List<ModelCommand> commands) {
        this.commands = commands;
    }

    public List<FirmwareVersion> getFirmwareVersions() {
        return firmwareVersions;
    }

    public void setFirmwareVersions(List<FirmwareVersion> firmwareVersions) {
        this.firmwareVersions = firmwareVersions;
    }

    @Override
    public String toString() {
        return "Model{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", manufacturer='" + manufacturer + '\'' +
                ", readings=" + readings +
                ", firmwareVersions='" + firmwareVersions + '\'' +
                '}';
    }
}
