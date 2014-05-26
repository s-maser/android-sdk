package com.relayr.core.user;

import com.relayr.Relayr_Application;
import com.relayr.Relayr_SDK;
import com.relayr.core.activity.Relayr_LoginActivity;
import com.relayr.core.event_listeners.LoginEventListener;

import android.app.Activity;
import android.content.Intent;

public class Relayr_User {

	private static String token;

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
}
