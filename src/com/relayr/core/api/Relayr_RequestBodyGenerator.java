package com.relayr.core.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import com.google.gson.Gson;
import com.relayr.core.error.Relayr_Exception;
import com.relayr.core.settings.Relayr_SDKSettings;
import com.relayr.core.settings.Relayr_SDKStatus;

public class Relayr_RequestBodyGenerator {

	private static String RELAYR_TITLEPARAM = "title";
	private static String RELAYR_OWNERPARAM = "owner";
	private static String RELAYR_MODELPARAM = "model";
	private static String RELAYR_FIRMWAREPARAM = "firmwareVersion";
	private static String RELAYR_DESCRIPTIONPARAM = "description";
	private static String RELAYR_CODEPARAM = "code";
	private static String RELAYR_CLIENTIDPARAM = "client_id";
	private static String RELAYR_CLIENTSECRETPARAM = "client_secret";
	private static String RELAYR_GRANTTYPEPARAM = "grant_type";
	private static String RELAYR_REDIRECTURLPARAM = "redirect_uri";
	private static String RELAYR_SCOPEPARAM = "scope";

	private static String RELAYR_DEFAULTGRANTTYPE = "authorization_code";

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

	public static List<NameValuePair> generateParameters(Relayr_ApiCall call, Object[] params) throws Exception {
		switch(call) {
		case UserToken: {
			List<NameValuePair> parameters = new ArrayList<NameValuePair>();
            parameters.add(new BasicNameValuePair(RELAYR_CODEPARAM, (String) params[0]));
            parameters.add(new BasicNameValuePair(RELAYR_CLIENTIDPARAM, (String) Relayr_SDKSettings.getClientId()));
            parameters.add(new BasicNameValuePair(RELAYR_CLIENTSECRETPARAM, (String) Relayr_SDKSettings.getClientSecret()));
            parameters.add(new BasicNameValuePair(RELAYR_GRANTTYPEPARAM, RELAYR_DEFAULTGRANTTYPE));
            parameters.add(new BasicNameValuePair(RELAYR_REDIRECTURLPARAM, Relayr_APICommons.ACCESS_REDIRECTION_URI));
            parameters.add(new BasicNameValuePair(RELAYR_SCOPEPARAM, ""));
            return parameters;
		}
		default: return null;
		}
	}

}
