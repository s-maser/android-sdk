package io.relayr.core.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Model implements Serializable {

    /** Auto generated uid */
	private static final long serialVersionUID = 1L;
	private String id;
	private String name;
	private String manufacturer;
	private ArrayList<HashMap<String,Object>> readings;
	private String firmwareVersion;

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

	public ArrayList<HashMap<String,Object>> getReadings() {
		return readings;
	}

	public void setReadings(ArrayList<HashMap<String,Object>> readings) {
		this.readings = readings;
	}

	public String getFirmwareVersion() {
		return firmwareVersion;
	}

	public void setFirmwareVersion(String firmwareVersion) {
		this.firmwareVersion = firmwareVersion;
	}

    @Override
    public String toString() {
        return "Model{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", manufacturer='" + manufacturer + '\'' +
                ", readings=" + readings +
                ", firmwareVersion='" + firmwareVersion + '\'' +
                '}';
    }
}
