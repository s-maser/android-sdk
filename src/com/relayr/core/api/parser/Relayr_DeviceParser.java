package com.relayr.core.api.parser;

import org.json.JSONObject;

import com.google.gson.Gson;
import com.relayr.core.device.Relayr_Device;

public class Relayr_DeviceParser {

	public static Relayr_Device parse(JSONObject json) throws Exception {
		Relayr_Device device = new Gson().fromJson(json.toString(), Relayr_Device.class);

		return device;
	}
}
