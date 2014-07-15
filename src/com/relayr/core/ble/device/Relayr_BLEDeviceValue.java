package com.relayr.core.ble.device;

import org.json.JSONObject;

public class Relayr_BLEDeviceValue {

	private byte[] rawValue;
	private JSONObject formattedValue;

	public Relayr_BLEDeviceValue(byte[] rawValue, JSONObject formattedValue) {
		this.rawValue = rawValue;
		this.formattedValue = formattedValue;
	}

	public byte[] getRawValue() {
		return rawValue;
	}

	public JSONObject getFormattedValue() {
		return formattedValue;
	}

}
