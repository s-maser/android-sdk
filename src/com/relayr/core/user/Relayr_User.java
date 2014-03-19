package com.relayr.core.user;

import com.relayr.Relayr_Application;
import com.relayr.core.activity.Relayr_LoginActivity;

import android.app.Activity;
import android.content.Intent;

public class Relayr_User {

	private static String userId;

	public static void setUserID(String id) {
		userId = id;
	}

	public static String getUserId() {
		return userId;
	}

	public static void login() {
		Activity currentActivity = Relayr_Application.currentActivity();
		Intent loginActivity = new Intent(currentActivity, Relayr_LoginActivity.class);
		currentActivity.startActivity(loginActivity);
	}
}
