package io.relayr.core.api.parser;

import org.json.JSONObject;

import com.google.gson.Gson;
import io.relayr.core.device.Relayr_DeviceModel;

public class Relayr_DeviceModelParser {

	public static Relayr_DeviceModel parse(JSONObject json) throws Exception {
		Relayr_DeviceModel deviceModel = new Gson().fromJson(json.toString(), Relayr_DeviceModel.class);

		return deviceModel;
	}
}
