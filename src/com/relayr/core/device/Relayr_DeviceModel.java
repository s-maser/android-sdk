package com.relayr.core.device;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Relayr_DeviceModel implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private String id;
	private String name;
	private String manufacturer;
	private ArrayList<HashMap<String,Object>> readings;

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

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append("[\n" +
						"\tid:\t" + getId() + "\n" +
						"\tname:\t" + getName() + "\n" +
						"\tmanufacturer:\t" + getManufacturer() + "\n" +
						"\treadings:\t" + getReadings().toString() + "\n" +
						"]");

		return stringBuilder.toString();
	}
}
