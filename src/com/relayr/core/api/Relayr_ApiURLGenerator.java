package com.relayr.core.api;

import java.util.HashMap;

import android.net.Uri;
import android.net.Uri.Builder;

import com.relayr.core.error.Relayr_Exception;
import com.relayr.core.settings.Relayr_SDKSettings;

public class Relayr_ApiURLGenerator {

	private final static String RELAYR_URLBASE = "http://relayr.apiary.io";
	private final static String RELAYR_USERTAG = "/user";
	private final static String RELAYR_CONNECTTAG = "/connect";
	private final static String RELAYR_DEVICESTAG = "/devices";
	private final static String RELAYR_CONFIGTAG = "/config";

	private final static String RELAYR_TOKENPARAM = "token";
	private final static String RELAYR_TYPEPARAM = "type";

	public static String generate(Relayr_ApiCall call, Object... params) throws Relayr_Exception {
		String urlString = RELAYR_URLBASE;

		switch (call) {
		case UserConnectWithoutToken: {
			urlString += RELAYR_USERTAG + RELAYR_CONNECTTAG;
			break;
		}
		case UserConnectWithToken: {
			String token = getUserToken();
			urlString += RELAYR_USERTAG + RELAYR_CONNECTTAG + "/" + token;
			break;
		}
		case ListAllDevices: {
			HashMap<String, Object> parametersCollection = new HashMap<String, Object>();
			String token = getUserToken();
			parametersCollection.put(RELAYR_TOKENPARAM, token);
			if (params.length > 0) {
				parametersCollection.put(RELAYR_TYPEPARAM, params[0]);
			}
			urlString += RELAYR_USERTAG + RELAYR_DEVICESTAG;
			urlString = Relayr_ApiURLGenerator.addParametersToUri(urlString, parametersCollection);
			break;
		}
		case ListClientDevices:
		case AddDevice: {
			String token = getUserToken();
			urlString += RELAYR_DEVICESTAG + "/" + token + "/";
			break;
		}
		case RetrieveDevice:
		case ModifyDevice:
		case RemoveDevice: {
			HashMap<String, Object> parametersCollection = new HashMap<String, Object>();
			String token = getUserToken();
			parametersCollection.put(RELAYR_TOKENPARAM, token);
			urlString += RELAYR_DEVICESTAG + "/" + params[0];
			urlString = Relayr_ApiURLGenerator.addParametersToUri(urlString, parametersCollection);
			break;
		}
		case RetrieveDeviceConfiguration:
		case ConfigureDevice:
		case DeleteDevice: {
			HashMap<String, Object> parametersCollection = new HashMap<String, Object>();
			String token = getUserToken();
			parametersCollection.put(RELAYR_TOKENPARAM, token);
			urlString += RELAYR_DEVICESTAG + "/" + params[0] + RELAYR_CONFIGTAG;
			urlString = Relayr_ApiURLGenerator.addParametersToUri(urlString, parametersCollection);
			break;
		}
		}

		return urlString;
	}

	private static String addParametersToUri(final String url, final HashMap<String, Object> params) {
		Builder uriBuilder;
		uriBuilder = Uri.parse(url).buildUpon();
		uriBuilder.path(url);
		for (String value:params.keySet()) {
			uriBuilder.appendQueryParameter(value, (String) params.get(value));
		}
		Uri uri = uriBuilder.build();
		return uri.toString();
	}

	private static String getUserToken() throws Relayr_Exception {
		String token = Relayr_SDKSettings.getUserToken();
		if (token == null) {
			throw new Relayr_Exception("Relayr user has not rights.");
		}
		return token;
	}
}
