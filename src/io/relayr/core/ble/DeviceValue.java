package io.relayr.core.ble;

import org.json.JSONObject;

class DeviceValue {

	private final byte[] rawValue;
	private final JSONObject formattedValue;

	public DeviceValue(byte[] rawValue, JSONObject formattedValue) {
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
