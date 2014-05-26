package com.relayr.core.api;

import java.util.HashMap;

import com.google.gson.Gson;
import com.relayr.core.error.Relayr_Exception;
import com.relayr.core.settings.Relayr_SDKSettings;
import com.relayr.core.user.Relayr_User;

public class Relayr_RequestBodyGenerator {

	private final static String RELAYR_IDFIELD = "id";
	private final static String RELAYR_DEVICEIDFIELD = "deviceid";

	public static String generateBody(Relayr_ApiCall call, Object[] params) throws Relayr_Exception {
		HashMap<String,Object> body = new HashMap<String,Object>();

		switch(call) {
		case AddDevice: {
			body.put(RELAYR_DEVICEIDFIELD, params[0]);
		}
		break;

		case ModifyDevice: {
			body = (HashMap<String,Object>)params[1];
			body.put(RELAYR_IDFIELD, Relayr_User.getUserToken());
		}
		break;

		case ConfigureDevice: {
			body = (HashMap<String,Object>)params[1];
		}
		break;

		case UserAuthorization:
		case ListAllDevices:
		case ListClientDevices:
		case RetrieveDevice:
		case RemoveDevice:
		case RetrieveDeviceConfiguration:
		case DeleteDevice:
			return null;

		default: return null;
		}

		Gson gson = new Gson();
		String jsonBody = gson.toJson(body);
		return jsonBody;
	}

}
