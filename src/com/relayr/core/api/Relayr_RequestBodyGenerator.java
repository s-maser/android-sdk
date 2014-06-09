package com.relayr.core.api;

import java.util.HashMap;

import com.google.gson.Gson;
import com.relayr.core.error.Relayr_Exception;
import com.relayr.core.settings.Relayr_SDKStatus;

public class Relayr_RequestBodyGenerator {

	private static String RELAYR_TITLEPARAM = "title";
	private static String RELAYR_OWNERPARAM = "owner";
	private static String RELAYR_MODELPARAM = "model";
	private static String RELAYR_FIRMWAREPARAM = "firmwareVersion";
	private static String RELAYR_DESCRIPTIONPARAM = "description";

	public static String generateBody(Relayr_ApiCall call, Object[] params) throws Relayr_Exception {

		switch(call) {
		case UpdateDeviceInfo:
		case UpdateUserInfo: {
			HashMap<String,Object> attributes = (HashMap<String,Object>) params[0];
			String body = new Gson().toJson(attributes);
			return body;
		}
		case RegisterDevice: {
			HashMap<String,Object> attributes = new HashMap<String,Object>();
			attributes.put(RELAYR_TITLEPARAM, params[0]);
			attributes.put(RELAYR_OWNERPARAM, Relayr_SDKStatus.getCurrentUser().getId());
			attributes.put(RELAYR_MODELPARAM, params[1]);
			attributes.put(RELAYR_FIRMWAREPARAM, params[2]);
			attributes.put(RELAYR_DESCRIPTIONPARAM, params[3]);
			String body = new Gson().toJson(attributes);
			return body;
		}
		default: return null;
		}

	}

}
