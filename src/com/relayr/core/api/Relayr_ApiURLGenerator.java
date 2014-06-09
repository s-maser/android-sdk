package com.relayr.core.api;

import java.util.HashMap;

import android.net.Uri;
import android.net.Uri.Builder;
import android.util.Log;

import com.relayr.core.app.Relayr_App;
import com.relayr.core.error.Relayr_Exception;
import com.relayr.core.settings.Relayr_SDKSettings;
import com.relayr.core.settings.Relayr_SDKStatus;
import com.relayr.core.user.Relayr_User;

public class Relayr_ApiURLGenerator {

	private final static String RELAYR_URLBASE = "http://api.relayr.io/";
	private final static String RELAYR_USERSTAG = "/users";
	private final static String RELAYR_DEVICESTAG = "/devices";
	private final static String RELAYR_OAUTH2TAG = "/oauth2";
	private final static String RELAYR_AUTHENTICATIONTAG = "/auth";
	private final static String RELAYR_USERINFOTAG = "/user-info";
	private final static String RELAYR_APPSTAG = "/apps";
	private final static String RELAYR_APPINFOTAG = "/app-info";
	private final static String RELAYR_DEVICEMODELSTAG = "/device-models";

	private final static String RELAYR_CLIENTIDPARAM = "client_id";
	private final static String RELAYR_REDIRECTURIPARAM = "redirect_uri";
	private final static String RELAYR_RESPONSETYPEPARAM = "response_type";
	private final static String RELAYR_SCOPEPARAM = "scope";
	private final static String RELAYR_MEANINGPARAM = "meaning";

	private final static String RELAYR_DEFAULTRESPONSETYPE = "token";
	private final static String RELAYR_DEFAULTSCOPE = "access-own-user-info";

	public static String generate(Relayr_ApiCall call, Object... params) throws Relayr_Exception {
		StringBuilder urlString = new StringBuilder();
		HashMap<String, Object> parametersCollection = new HashMap<String, Object>();

		switch (call) {
		case UserAuthorization: {
			urlString.append(RELAYR_OAUTH2TAG);
			urlString.append(RELAYR_AUTHENTICATIONTAG);
			parametersCollection.put(RELAYR_CLIENTIDPARAM, Relayr_SDKSettings.getAppKey());
			parametersCollection.put(RELAYR_REDIRECTURIPARAM, Relayr_APICommons.DEFAULT_REDIRECTION_URI);
			parametersCollection.put(RELAYR_RESPONSETYPEPARAM, RELAYR_DEFAULTRESPONSETYPE);
			parametersCollection.put(RELAYR_SCOPEPARAM, RELAYR_DEFAULTSCOPE);
			break;
		}
		case UserInfo: {
			urlString.append(RELAYR_OAUTH2TAG);
			urlString.append(RELAYR_USERINFOTAG);
			break;
		}
		case UpdateUserInfo: {
			urlString.append(RELAYR_USERSTAG);
			urlString.append("/");
			Relayr_User user = Relayr_SDKStatus.getCurrentUser();
			urlString.append(((user != null)? user.getId():""));
			break;
		}
		case UserDevices: {
			urlString.append(RELAYR_USERSTAG);
			urlString.append("/");
			Relayr_User user = Relayr_SDKStatus.getCurrentUser();
			urlString.append(((user != null)? user.getId():""));
			urlString.append(RELAYR_DEVICESTAG);
			if (params.length > 0) {
				parametersCollection.put(RELAYR_MEANINGPARAM, params[0]);
			}
			break;
		}
		case DeviceInfo:
		case UpdateDeviceInfo: {
			urlString.append(RELAYR_DEVICESTAG);
			urlString.append("/");
			urlString.append(params[0]);
			break;
		}
		case ConnectDeviceToApp:
		case DisconnectDeviceFromApp: {
			urlString.append(RELAYR_DEVICESTAG);
			urlString.append("/");
			urlString.append(params[0]);
			urlString.append(RELAYR_APPSTAG);
			urlString.append("/");
			Relayr_App app = Relayr_SDKStatus.getCurrentApp();
			urlString.append(((app != null)? app.getId():""));
			break;
		}
		case AppInfo: {
			urlString.append(RELAYR_OAUTH2TAG);
			urlString.append(RELAYR_APPINFOTAG);
			break;
		}
		case DeviceModels: {
			urlString.append(RELAYR_DEVICEMODELSTAG);
			break;
		}
		case DeviceModelInfo: {
			urlString.append(RELAYR_DEVICEMODELSTAG);
			urlString.append("/");
			urlString.append(params[0]);
			break;
		}
		case RegisterDevice: {
			urlString.append(RELAYR_DEVICESTAG);
			break;
		}
		}

		String uriString = Relayr_ApiURLGenerator.addParametersToUri(urlString.toString(), parametersCollection);
		Log.d("Relayr_ApiURLGenerator", "Generated url: " + uriString);
		return uriString;
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

}
