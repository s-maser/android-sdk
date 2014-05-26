package com.relayr.core.api;

import java.util.HashMap;

import android.net.Uri;
import android.net.Uri.Builder;
import android.util.Log;

import com.relayr.core.error.Relayr_Exception;
import com.relayr.core.settings.Relayr_SDKSettings;
import com.relayr.core.user.Relayr_User;

public class Relayr_ApiURLGenerator {

	private final static String RELAYR_URLBASE = "http://api.relayr.io/";
	private final static String RELAYR_USERTAG = "/user";
	private final static String RELAYR_DEVICESTAG = "/devices";
	private final static String RELAYR_CONFIGTAG = "/config";
	private final static String RELAYR_OAUTH2TAG = "/oauth2";
	private final static String RELAYR_AUTHENTICATIONTAG = "/auth";

	private final static String RELAYR_TOKENPARAM = "token";
	private final static String RELAYR_TYPEPARAM = "type";
	private final static String RELAYR_CLIENTIDPARAM = "client_id";
	private final static String RELAYR_REDIRECTURIPARAM = "redirect_uri";
	private final static String RELAYR_RESPONSETYPEPARAM = "response_type";
	private final static String RELAYR_SCOPEPARAM = "scope";

	private final static String RELAYR_DEFAULTRESPONSETYPE = "token";
	private final static String RELAYR_DEFAULTSCOPE = "access-own-user-info";

	public static String generate(Relayr_ApiCall call, Object... params) throws Relayr_Exception {
		String urlString = "";
		HashMap<String, Object> parametersCollection = new HashMap<String, Object>();

		switch (call) {
		case UserAuthorization: {
			urlString += RELAYR_OAUTH2TAG + RELAYR_AUTHENTICATIONTAG;
			parametersCollection.put(RELAYR_CLIENTIDPARAM, Relayr_SDKSettings.getAppKey());
			parametersCollection.put(RELAYR_REDIRECTURIPARAM, Relayr_APICommons.DEFAULT_REDIRECTION_URI);
			parametersCollection.put(RELAYR_RESPONSETYPEPARAM, RELAYR_DEFAULTRESPONSETYPE);
			parametersCollection.put(RELAYR_SCOPEPARAM, RELAYR_DEFAULTSCOPE);
			break;
		}
		case ListAllDevices: {
			String token = getUserToken();
			parametersCollection.put(RELAYR_TOKENPARAM, token);
			if (params.length > 0) {
				parametersCollection.put(RELAYR_TYPEPARAM, params[0]);
			}
			urlString += RELAYR_USERTAG + RELAYR_DEVICESTAG;
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
			String token = getUserToken();
			parametersCollection.put(RELAYR_TOKENPARAM, token);
			urlString += RELAYR_DEVICESTAG + "/" + params[0];
			break;
		}
		case RetrieveDeviceConfiguration:
		case ConfigureDevice:
		case DeleteDevice: {
			String token = getUserToken();
			parametersCollection.put(RELAYR_TOKENPARAM, token);
			urlString += RELAYR_DEVICESTAG + "/" + params[0] + RELAYR_CONFIGTAG;
			break;
		}
		}

		urlString = Relayr_ApiURLGenerator.addParametersToUri(urlString, parametersCollection);
		Log.d("Relayr_ApiURLGenerator", "Generated url: " + urlString);
		return urlString;
	}

	private static String addParametersToUri(final String url, final HashMap<String, Object> params) {
		Builder uriBuilder;
		uriBuilder = Uri.parse(RELAYR_URLBASE).buildUpon();
		uriBuilder.path(url);
		for (String value:params.keySet()) {
			uriBuilder.appendQueryParameter(value, (String) params.get(value));
		}
		Uri uri = uriBuilder.build();
		return uri.toString();
	}

	private static String getUserToken() throws Relayr_Exception {
		String token = Relayr_User.getUserToken();
		if (token == null) {
			throw new Relayr_Exception("Relayr user has not rights.");
		}
		return token;
	}
}
