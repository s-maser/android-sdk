package com.relayr.core.settings;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.relayr.Relayr_Application;
import com.relayr.Relayr_SDK;
import com.relayr.core.activity.Relayr_LoginActivity;
import com.relayr.core.api.Relayr_ApiCall;
import com.relayr.core.api.Relayr_ApiConnector;
import com.relayr.core.app.Relayr_App;
import com.relayr.core.error.Relayr_Exception;
import com.relayr.core.event_listeners.LoginEventListener;
import com.relayr.core.user.Relayr_User;

public class Relayr_SDKStatus {

	static boolean active = false;
	static boolean backgroundMode = false;
	static Relayr_User currentUser;
	static Relayr_App currentApp;
	static String userToken;

	public static boolean isActive() {
		return active;
	}

	public static void setActive(boolean status) {
		active = status;
	}

	public static boolean isBackgroundModeActive() {
		return backgroundMode;
	}

	public static void setBackgroundMode(boolean status) {
		backgroundMode = status;
	}

	public static Relayr_User getCurrentUser() {
		return currentUser;
	}

	public static void setCurrentUser(Relayr_User currentUser) {
		Relayr_SDKStatus.currentUser = currentUser;
	}

	public static Relayr_App getCurrentApp() {
		return currentApp;
	}

	public static void setCurrentApp(Relayr_App currentApp) {
		Relayr_SDKStatus.currentApp = currentApp;
	}

	public static String getUserToken() {
		return userToken;
	}

	public static void setUserToken(String userToken) {
		Relayr_SDKStatus.userToken = userToken;
	}

	public static boolean isUserLogged() {
		return Relayr_SDKStatus.getUserToken() != null;
	}

	public static void login() {
		Activity currentActivity = Relayr_Application.currentActivity();
		Intent loginActivity = new Intent(currentActivity, Relayr_LoginActivity.class);
		currentActivity.startActivity(loginActivity);
	}

	public static boolean logout() {
		Relayr_SDKStatus.setUserToken(null);
		LoginEventListener listener = Relayr_SDK.getLoginEventListener();
		if (listener != null) {
			listener.onUserLoggedOutSuccessfully();
		}
		return (Relayr_SDKStatus.getUserToken() == null);
	}

	public static void synchronizeUserInfo() throws Relayr_Exception {
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				try {
					Object[] parameters = {};
					Relayr_ApiConnector.doCall(Relayr_ApiCall.UserInfo, parameters);
				} catch (Relayr_Exception e) {
					Log.d("Relayr_SDKStatus", "Error: " + e.getMessage());
				}

				return null;
			}
		}.execute();
	}

	public static void synchronizeAppInfo() throws Relayr_Exception {
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				try {
					Object[] parameters = {};
					Relayr_ApiConnector.doCall(Relayr_ApiCall.AppInfo, parameters);
				} catch (Relayr_Exception e) {
					Log.d("Relayr_SDKStatus", "Error: " + e.getMessage());
				}
				return null;
			}
		}.execute();
	}

}
