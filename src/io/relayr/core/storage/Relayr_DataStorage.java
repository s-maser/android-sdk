package io.relayr.core.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.google.gson.Gson;

import io.relayr.RelayrApp;
import io.relayr.core.settings.Relayr_SDKStatus;
import io.relayr.core.user.Relayr_User;


public class Relayr_DataStorage {

	static SharedPreferences localStorage;
	static String storageIdentifier = "relayrPreferences";
	static String tokenField = "RELAYR_TOKEN";
	static String userIDField = "RELAYR_USER";

	public static void saveLocalData() {
		localStorage = RelayrApp.get().getSharedPreferences(storageIdentifier, Context.MODE_PRIVATE);
		Editor edit = localStorage.edit();
		edit.putString(tokenField, Relayr_SDKStatus.getUserToken());
		Relayr_User user = Relayr_SDKStatus.getCurrentUser();
		String userString = null;
		if (user != null) {
			userString = new Gson().toJson(user).toString();
		}
		edit.putString(userIDField, userString);
		edit.apply();
	}

	public static void loadLocalData() {
		localStorage = RelayrApp.get().getSharedPreferences(storageIdentifier, Context.MODE_PRIVATE);
		Relayr_SDKStatus.setUserToken(localStorage.getString(tokenField, null));
		String userString = localStorage.getString(userIDField, null);
		if (userString != null) {
			Relayr_SDKStatus.setCurrentUser(new Gson().fromJson(userString, Relayr_User.class));
		} else {
			Relayr_SDKStatus.setCurrentUser(null);
		}
	}
}
