package io.relayr.ble;

import org.json.JSONObject;

class BleDeviceValue {

	private final byte[] rawValue;
	private final JSONObject formattedValue;

	public BleDeviceValue(byte[] rawValue, JSONObject formattedValue) {
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
