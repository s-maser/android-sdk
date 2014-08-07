package io.relayr.core.settings;

import android.os.AsyncTask;
import android.util.Log;

import io.relayr.Relayr_SDK;
import io.relayr.core.activity.Relayr_LoginActivity;
import io.relayr.core.api.Relayr_ApiCall;
import io.relayr.core.api.Relayr_ApiConnector;
import io.relayr.core.app.Relayr_App;
import io.relayr.core.error.Relayr_Exception;
import io.relayr.core.event_listeners.LoginEventListener;
import io.relayr.core.user.Relayr_User;

public class Relayr_SDKStatus {

	static boolean active;
	static boolean backgroundMode;
	static Relayr_User currentUser;
	static Relayr_App currentApp;
	static String userToken;

	static {
		active = false;
		backgroundMode = false;
	}

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

	public static void synchronizeTokenInfo(final Relayr_LoginActivity activity,
                                            final String accessCode)
            throws Relayr_Exception {
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				try {
					Object[] parameters = {accessCode};
					Relayr_ApiConnector.doCall(Relayr_ApiCall.UserToken, parameters);
					final LoginEventListener listener = Relayr_SDK.getLoginEventListener();
					if (listener != null) {
                        activity.runOnUiThread(new Runnable(){
							public void run() {
								listener.onUserLoggedInSuccessfully();
							}
						});
					}
					Relayr_SDKStatus.synchronizeUserInfo();
				} catch (Relayr_Exception e) {
					Log.d("Relayr_SDKStatus", "Error: " + e.getMessage());
				}
				return null;
			}
		}.execute();
	}

}
