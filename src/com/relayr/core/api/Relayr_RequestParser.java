package com.relayr.core.api;

import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.relayr.core.error.Relayr_Exception;
import com.relayr.core.user.Relayr_User;
import com.relayr.core.api.parser.Relayr_DeviceParser;
import com.relayr.core.device.Relayr_Device;

public class Relayr_RequestParser {

	private static String RELAYR_INTERNALERRORMESSAGE = "Internal service error. Try in a few minutes.";
	private static String RELAYR_INTERNETCONNECTIONERRORMESSAGE = "Not posible to reach Relayr server. Check your internet settings. You will be identified as an anonymous user.";
	private static String RELAYR_UNKNOWNERRORMESSAGE = "Relayr unknown error";

	private static String RELAYR_ERRORMESSAGEFIELD = "error";

	public static Object parse(Relayr_ApiCall call, HttpResponse response) throws Exception {
		HttpEntity responseEntity = response.getEntity();
		String content = (responseEntity != null)? EntityUtils.toString(response.getEntity()) : null;
		Log.d("Relayr_RequestParser", "Response: " + content);
		checkResponseCode(response, content);
		Object parsedObject = parseData(call, content);
		return parsedObject;
	}

	private static void checkResponseCode(HttpResponse response, String content) throws Exception {
		int statusCode = response.getStatusLine().getStatusCode();
		Log.d("Relayr_RequestParser", "Response status code: " + statusCode);
		JSONObject jsonContent;
		if (content != null)
			try {
				jsonContent = new JSONObject(content);
			} catch (JSONException e) {
				jsonContent = null;
			}
		else
			jsonContent = null;

		switch (statusCode) {
		case 0: {
			throw new Relayr_Exception(RELAYR_INTERNETCONNECTIONERRORMESSAGE);
		}
		case 200: {
			break;
		}
		case 401: {
			Relayr_User.login();
			break;
		}
		case 403: {
			Relayr_User.login();
			break;
		}
		case 500: {
			throw new Relayr_Exception(RELAYR_INTERNALERRORMESSAGE);
		}
		default: {
			if ((jsonContent != null) && (jsonContent.has(RELAYR_ERRORMESSAGEFIELD))) {
				throw new Relayr_Exception(jsonContent.getString(RELAYR_ERRORMESSAGEFIELD));
			} else {
				throw new Relayr_Exception(jsonContent.getString(RELAYR_UNKNOWNERRORMESSAGE));
			}
		}
		}
	}

	private static Object parseData(Relayr_ApiCall call, String content) throws Relayr_Exception {
		try {
			switch (call) {
			/*case AddDevice:
			case ModifyDevice:
			case RemoveDevice:
			case ConfigureDevice:
			case DeleteDevice: {
				return Boolean.valueOf(true);
			}
			case ListAllDevices:
			case ListClientDevices: {
				JSONArray json = new JSONArray(content);
				return json;
			}
			case RetrieveDevice:
			case RetrieveDeviceConfiguration:*/
			case UserInfo: {
				Log.d("Relayr_RequestParser", "Parsing UserInfo");
				JSONObject json = new JSONObject(content);
				return json;
			}

			case UserDevices: {
				Log.d("Relayr_RequestParser", "Parsing UserDevices");
				ArrayList<Relayr_Device> devices = new Gson().fromJson(content, new TypeToken<ArrayList<Relayr_Device>>(){}.getType());
				return devices;
			}

			default: return content;
			}
		} catch (JSONException e) {
			throw new Relayr_Exception(e.getMessage());
		}
	}
}
