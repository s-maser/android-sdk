package com.relayr.core.api;

import java.util.HashMap;

import com.google.gson.Gson;
import com.relayr.core.error.Relayr_Exception;

public class Relayr_RequestBodyGenerator {

	public static String generateBody(Relayr_ApiCall call, Object[] params) throws Relayr_Exception {

		switch(call) {
		case UpdateDeviceInfo: {
			HashMap<String,Object> attributes = (HashMap<String,Object>) params[1];
			String body = new Gson().toJson(attributes);
			return body;
		}
		default: return null;
		}

	}

}
