package com.relayr.core.device;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Relayr_DeviceModel {

	private String id;
	private String name;
	private String manufacturer;
	private ArrayList<HashMap> readings;

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

	public ArrayList<HashMap> getReadings() {
		return readings;
	}

	public void setReadings(ArrayList<HashMap> readings) {
		this.readings = readings;
	}

	@Override
	public String toString() {
		String message = new String();

		message += 	"[\n" +
						"\tid:\t" + getId() + "\n" +
						"\tname:\t" + getName() + "\n" +
						"\tmanufacturer:\t" + getManufacturer() + "\n" +
						"\treadings:\t" + getReadings().toString() + "\n" +
					"]";

		return message;
	}
}
