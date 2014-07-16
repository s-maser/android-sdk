package com.relayr.core.ble.device;

import org.json.JSONException;
import org.json.JSONObject;

public class Relayr_BLEDeviceDataAdapter {

	protected static String COLOR_DATA_INDEX = "clr";
	protected static String R_COLOR_DATA_INDEX = "r";
	protected static String G_COLOR_DATA_INDEX = "g";
	protected static String B_COLOR_DATA_INDEX = "b";
	protected static String PROXIMITY_DATA_INDEX = "prox";
	protected static String LIGHT_DATA_INDEX = "light";
	protected static String ACCELEROMETER_DATA_INDEX = "accel";
	protected static String X_ACCELEROMETER_DATA_INDEX = "x";
	protected static String Y_ACCELEROMETER_DATA_INDEX = "y";
	protected static String Z_ACCELEROMETER_DATA_INDEX = "z";
	protected static String GYROSCOPE_DATA_INDEX = "gyro";
	protected static String X_GYROSCOPE_DATA_INDEX = "x";
	protected static String Y_GYROSCOPE_DATA_INDEX = "y";
	protected static String Z_GYROSCOPE_DATA_INDEX = "z";
	protected static String HUMIDITY_DATA_INDEX = "hum";
	protected static String TEMPERATURE_DATA_INDEX = "temp";
	protected static String SOUND_LEVEL_DATA_INDEX = "snd_level";
	protected static String TIMESTAMP_DATA_INDEX = "ts";

	protected static JSONObject getLIGHTSensorData(byte[] value) {
		JSONObject returnValue = initResponseData();
		try {
			if (value != null) {
				int c = (byteToUnsignedInt(value[1]) << 8) | byteToUnsignedInt(value[0]);
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
				returnValue.put(LIGHT_DATA_INDEX, c);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return returnValue;
	}

	protected static JSONObject getGYROSensorData(byte[] value) {
		JSONObject returnValue = initResponseData();
		try {
			if (value != null) {
				int gyroX = (byteToUnsignedInt(value[0]) << 8) | byteToUnsignedInt(value[1]);
				int gyroY = (byteToUnsignedInt(value[2]) << 8) | byteToUnsignedInt(value[3]);
				int gyroZ = (byteToUnsignedInt(value[4]) << 8) | byteToUnsignedInt(value[5]);
				int accX = (byteToUnsignedInt(value[6]) << 8) | byteToUnsignedInt(value[7]);
				int accY = (byteToUnsignedInt(value[8]) << 8) | byteToUnsignedInt(value[9]);
				int accZ = (byteToUnsignedInt(value[10]) << 8) | byteToUnsignedInt(value[11]);

				if (gyroX > 32768) gyroX = -(gyroX - 32769);
				if (gyroY > 32768) gyroY = -(gyroY - 32769);
				if (gyroZ > 32768) gyroZ = -(gyroZ - 32769);
				if (accX > 32768) accX = -(accX - 32769);
				if (accY > 32768) accY = -(accY - 32769);
				if (accZ > 32768) accZ = -(accZ - 32769);

				float gyroXX = (float)gyroX;
				float gyroYY = (float)gyroY;
				float gyroZZ = (float)gyroZ;
				float accXX = (float)accX;
				float accYY = (float)accY;
				float accZZ = (float)accZ;

				gyroXX = gyroXX / 131.0f;
				gyroYY = gyroYY / 131.0f;
				gyroZZ = gyroZZ / 131.0f;
				accXX = accXX / 16384.0f;
				accYY = accYY / 16384.0f;
				accZZ = accZZ / 16384.0f;

				JSONObject gyroArray = new JSONObject();
				gyroArray.put(X_GYROSCOPE_DATA_INDEX, gyroXX);
				gyroArray.put(Y_GYROSCOPE_DATA_INDEX, gyroYY);
				gyroArray.put(Z_GYROSCOPE_DATA_INDEX, gyroZZ);
				returnValue.put(GYROSCOPE_DATA_INDEX, gyroArray);

				JSONObject accArray = new JSONObject();
				accArray.put(X_ACCELEROMETER_DATA_INDEX, accXX);
				accArray.put(Y_ACCELEROMETER_DATA_INDEX, accYY);
				accArray.put(Z_ACCELEROMETER_DATA_INDEX, accZZ);
				returnValue.put(ACCELEROMETER_DATA_INDEX, accArray);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return returnValue;
	}

	protected static JSONObject getHTUSensorData(byte[] value) {
		JSONObject returnValue = initResponseData();
		try {
			if (value != null) {
				int t = (byteToUnsignedInt(value[1]) << 8) | byteToUnsignedInt(value[0]);
				int h = (byteToUnsignedInt(value[3]) << 8) | byteToUnsignedInt(value[2]);

				float hh = (float)h / 100.0f;
				float tt = (float)t / 100.0f;

				returnValue.put(HUMIDITY_DATA_INDEX, hh);
				returnValue.put(TEMPERATURE_DATA_INDEX, tt);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return returnValue;
	}

	/*protected static JSONObject getMICSensorData(byte[] value) {
		JSONObject returnValue = new JSONObject();
		try {
			if (value != null) {
				int t = (byteToUnsignedInt(value[1]) << 8) | byteToUnsignedInt(value[0]);
				int h = (byteToUnsignedInt(value[3]) << 8) | byteToUnsignedInt(value[2]);

				float hh = (float)h / 100.0f;
				float tt = (float)t / 100.0f;

				returnValue.put(HUMIDITY_DATA_INDEX, hh);
				returnValue.put(TEMPERATURE_DATA_INDEX, tt);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return returnValue;
	}*/

	private static JSONObject initResponseData() {
		JSONObject returnedValue = new JSONObject();
		try {
			returnedValue.put(TIMESTAMP_DATA_INDEX, System.currentTimeMillis());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return returnedValue;
	}

	private static int byteToUnsignedInt(byte b) {
	    return (int) b & 0xff;
	}
}
