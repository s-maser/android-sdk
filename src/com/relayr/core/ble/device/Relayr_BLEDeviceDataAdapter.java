package com.relayr.core.ble.device;

import org.json.JSONException;
import org.json.JSONObject;

public class Relayr_BLEDeviceDataAdapter {

	protected static String COLOR_DATA_INDEX = "clr";
	protected static String R_COLOR_DATA_INDEX = "r";
	protected static String G_COLOR_DATA_INDEX = "g";
	protected static String B_COLOR_DATA_INDEX = "b";
	protected static String PROXIMITY_DATA_INDEX = "prox";

	public static JSONObject getColorSensorData(byte[] value) {
		JSONObject returnValue = new JSONObject();
		try {
			if (value != null) {
				int r = (byteToUnsignedInt(value[3]) << 8) | byteToUnsignedInt(value[2]);
				int g = (byteToUnsignedInt(value[5]) << 8) | byteToUnsignedInt(value[4]);
				int b = (byteToUnsignedInt(value[7]) << 8) | byteToUnsignedInt(value[6]);
				int p = (byteToUnsignedInt(value[9]) << 8) | byteToUnsignedInt(value[8]);

				float rr = (float)r;
				float gg = (float)g;
				float bb = (float)b;

				//relative correction
				rr *= 2.0/3.0;

				//normalize
				float max = Math.max(rr,Math.max(gg,bb));
				rr = (rr/max) * 255;
				gg = (gg/max) * 255;
				bb = (bb/max) * 255;

				JSONObject colorsArray = new JSONObject();

				colorsArray.put(R_COLOR_DATA_INDEX, rr);
				colorsArray.put(G_COLOR_DATA_INDEX, gg);
				colorsArray.put(B_COLOR_DATA_INDEX, bb);
				returnValue.put(COLOR_DATA_INDEX, colorsArray);
				returnValue.put(PROXIMITY_DATA_INDEX, p);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return returnValue;
	}

	private static int byteToUnsignedInt(byte b) {
	    return (int) b & 0xff;
	}
}
