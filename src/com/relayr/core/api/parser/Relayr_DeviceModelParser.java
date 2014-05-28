package com.relayr.core.api.parser;

import org.json.JSONObject;

import com.relayr.core.device.Relayr_DeviceModel;
import com.relayr.core.error.Relayr_Exception;

public class Relayr_DeviceModelParser {

	private static String RELAYR_IDFIELD = "id";
	private static String RELAYR_NAMEFIELD = "name";
	private static String RELAYR_MANUFACTURERFIELD = "manufacturer";
	private static String RELAYR_READINGSFIELD = "readings";

	public static Relayr_DeviceModel parse(JSONObject json) throws Exception {
		Relayr_DeviceModel deviceModel = new Relayr_DeviceModel();

		/*if (json.has(RELAYR_IDFIELD)) {
			deviceModel.setId(json.getString(RELAYR_IDFIELD));
		} else {
			throw new Relayr_Exception("Invalid device model data received from the server (no id received)");
		}
		if (json.has(RELAYR_NAMEFIELD)) {
			deviceModel.setName(json.getString(RELAYR_NAMEFIELD));
		}
		if (json.has(RELAYR_MANUFACTURERFIELD)) {
			deviceModel.setName(json.getString(RELAYR_MANUFACTURERFIELD));
		}
		if (json.has(RELAYR_READINGSFIELD)) {
			deviceModel.setReadings(json.getJSONArray(RELAYR_READINGSFIELD));
		}*/

		return deviceModel;
	}
}
