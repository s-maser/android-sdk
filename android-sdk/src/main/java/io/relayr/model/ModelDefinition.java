package io.relayr.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class ModelDefinition implements Serializable {

    /** Auto generated uid */
	private static final long serialVersionUID = 1L;
	public final String id;
    public final String name;
    public final String manufacturer;
    public final List<Map<String,Object>> readings;
    public final List<Map<String,Object>> firmwareVersions;

    public ModelDefinition(String id, String name, String manufacturer, List<Map<String, Object>>
            readings, List<Map<String, Object>> firmwareVersions) {
        this.id = id;
        this.name = name;
        this.manufacturer = manufacturer;
        this.readings = readings;
        this.firmwareVersions = firmwareVersions;
    }

    @Override
    public String toString() {
        return "ModelDefinition{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", manufacturer='" + manufacturer + '\'' +
                ", readings=" + readings +
                ", firmwareVersions=" + firmwareVersions +
                '}';
    }
}

