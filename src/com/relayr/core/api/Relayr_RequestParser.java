package com.relayr.core.api;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.relayr.core.error.Relayr_Exception;

public class Relayr_RequestParser {

	private static String RELAYR_INTERNALERRORMESSAGE = "Internal service error. Try in a few minutes.";
	private static String RELAYR_INTERNETCONNECTIONERRORMESSAGE = "Not posible to reach Relayr server. Check your internet settings. You will be identified as an anonymous user.";
	private static String RELAYR_DUMMYERRORMESSAGE = "Dummy error message";

	private static String RELAYR_TOKENRESPONSEFIELD = "token";

	public static Object parse(Relayr_ApiCall call, HttpResponse response) throws Relayr_Exception {
		try {
			checkResponseCode(response);
			HttpEntity responseEntity = response.getEntity();
			Object parsedObject;
			if (responseEntity == null) {
				parsedObject = parseData(call, null);
			} else {
				parsedObject = parseData(call, EntityUtils.toString(response.getEntity()));
			}
			return parsedObject;
		} catch (ParseException e) {
			throw new Relayr_Exception(e.getMessage());
		} catch (IOException e) {
			throw new Relayr_Exception(e.getMessage());
		}
	}

	private static void checkResponseCode(HttpResponse response) throws Relayr_Exception {
		int statusCode = response.getStatusLine().getStatusCode();

		switch (statusCode) {
		case 0: {
			throw new Relayr_Exception(RELAYR_INTERNETCONNECTIONERRORMESSAGE);
		}
		case 400: {
			throw new Relayr_Exception(RELAYR_DUMMYERRORMESSAGE);
		}
		case 401: {
			throw new Relayr_Exception(RELAYR_DUMMYERRORMESSAGE);
		}
		case 403: {
			throw new Relayr_Exception(RELAYR_DUMMYERRORMESSAGE);
		}
		case 404: {
			throw new Relayr_Exception(RELAYR_DUMMYERRORMESSAGE);
		}
		case 500: {
			throw new Relayr_Exception(RELAYR_INTERNALERRORMESSAGE);
		}
		default: {
			break;
		}
		}
	}

	private static Object parseData(Relayr_ApiCall call, String content) throws Relayr_Exception {
		try {
			switch (call) {
			case UserConnectWithoutToken: {
				JSONObject json = new JSONObject(content);
				return json.getString(RELAYR_TOKENRESPONSEFIELD);
			}
			case UserConnectWithToken:
			case AddDevice:
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
			case RetrieveDeviceConfiguration: {
				JSONObject json = new JSONObject(content);
				return json;
			}
			default: return content;
			}
		} catch (JSONException e) {
			throw new Relayr_Exception(e.getMessage());
		}
	}
}
