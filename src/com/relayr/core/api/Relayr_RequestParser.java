package com.relayr.core.api;

import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.relayr.core.device.Relayr_Device;
import com.relayr.core.error.Relayr_Exception;
import com.relayr.core.settings.Relayr_SDKStatus;
import com.relayr.core.user.Relayr_User;

public class Relayr_RequestParser {

	private static String RELAYR_INTERNALERRORMESSAGE = "Internal service error. Try in a few minutes.";
	private static String RELAYR_INTERNETCONNECTIONERRORMESSAGE = "Not posible to reach Relayr server. Check your internet settings. You will be identified as an anonymous user.";
	private static String RELAYR_UNKNOWNERRORMESSAGE = "Relayr unknown error";

	private static String RELAYR_ERRORMESSAGEFIELD = "error";
	private static String RELAYR_MESSAGEFIELD = "message";

	public static Object parse(Relayr_ApiCall call, HttpResponse response) throws Exception {
		HttpEntity responseEntity = response.getEntity();
		String content = (responseEntity != null)? EntityUtils.toString(response.getEntity()) : null;
		Log.d("Relayr_RequestParser", "Response: " + content);
		checkResponseCode(call, response, content);
		Object parsedObject = parseData(call, content);
		return parsedObject;
	}

	private static void checkResponseCode(Relayr_ApiCall call, HttpResponse response, String content) throws Exception {
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
			if (call == Relayr_ApiCall.UserInfo) {
				Relayr_SDKStatus.login();
			} else {
				generateError(jsonContent);
			}
			break;
		}
		case 403: {
			if (call == Relayr_ApiCall.UserInfo) {
				Relayr_SDKStatus.login();
			} else {
				generateError(jsonContent);
			}
			break;
		}
		case 500: {
			throw new Relayr_Exception(RELAYR_INTERNALERRORMESSAGE);
		}
		default: {
			generateError(jsonContent);
			break;
		}
		}
	}

	private static void generateError(JSONObject jsonContent) throws Exception {
		if (jsonContent != null) {
			if (jsonContent.has(RELAYR_ERRORMESSAGEFIELD)) {
				throw new Relayr_Exception(jsonContent.getString(RELAYR_ERRORMESSAGEFIELD));
			}
			if (jsonContent.has(RELAYR_MESSAGEFIELD)) {
				throw new Relayr_Exception(jsonContent.getString(RELAYR_MESSAGEFIELD));
			}
		} else {
			throw new Relayr_Exception(RELAYR_UNKNOWNERRORMESSAGE);
		}
	}

	private static Object parseData(Relayr_ApiCall call, String content) throws Relayr_Exception {
		switch (call) {
		case UserInfo: {
			Relayr_User currentUser = new Gson().fromJson(content, Relayr_User.class);
			Relayr_SDKStatus.setCurrentUser(currentUser);
			return true;
		}

		case UserDevices: {
			ArrayList<Relayr_Device> devices = new Gson().fromJson(content, new TypeToken<ArrayList<Relayr_Device>>(){}.getType());
			return devices;
		}

		case DeviceInfo:
		case UpdateDeviceInfo: {
			Relayr_Device device = new Gson().fromJson(content, Relayr_Device.class);
			return device;
		}

		default: return content;
		}
	}
}
