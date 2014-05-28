package com.relayr.core.api;

import com.relayr.core.error.Relayr_Exception;

public class Relayr_RequestBodyGenerator {

	public static String generateBody(Relayr_ApiCall call, Object[] params) throws Relayr_Exception {
		//HashMap<String,Object> body = new HashMap<String,Object>();

		switch(call) {
		/*case AddDevice: {
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
		break;*/

		default: return null;
		}

		/*Gson gson = new Gson();
		String jsonBody = gson.toJson(body);
		return jsonBody;*/
	}

}
