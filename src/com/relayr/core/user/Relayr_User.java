package com.relayr.core.user;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;

import com.relayr.Relayr_Application;
import com.relayr.Relayr_SDK;
import com.relayr.core.activity.Relayr_LoginActivity;
import com.relayr.core.api.Relayr_ApiCall;
import com.relayr.core.api.Relayr_ApiConnector;
import com.relayr.core.event_listeners.LoginEventListener;

public class Relayr_User {

	private static String RELAYR_IDPARAM = "id";

	private static String token;
	private static String id;

	public static void setToken(String newToken) {
		token = newToken;
	}

	public static String getUserToken() {
		return token;
	}

	public static boolean isUserLogged() {
		return token != null;
	}

	public static void login() {
		Activity currentActivity = Relayr_Application.currentActivity();
		Intent loginActivity = new Intent(currentActivity, Relayr_LoginActivity.class);
		currentActivity.startActivity(loginActivity);
	}

	public static boolean logout() {
		token = null;
		LoginEventListener listener = Relayr_SDK.getLoginEventListener();
		if (listener != null) {
			listener.onUserLoggedOutSuccessfully();
		}
		return (token == null);
	}

	public static void synchronizeUserInfo() throws Exception {
		Object[] parameters = {};
		try {
			JSONObject userInfo = (JSONObject)Relayr_ApiConnector.doCall(Relayr_ApiCall.UserInfo, parameters);
			if (userInfo.has(RELAYR_IDPARAM)) {
				id = userInfo.getString(RELAYR_IDPARAM);
			}
		} catch (Exception e) {
			throw e;
		}
	}

	public static void setUserID(String userID) {
		id = userID;
	}

	public static String getUserID() {
		return id;
	}
}
