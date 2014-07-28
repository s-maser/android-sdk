package io.relayr;

import android.app.Activity;

public class Relayr_Application {

	private static Activity activity;

	public static void setCurrentActivity(Activity currentActivity) {
	    activity = currentActivity;
	}

	public static Activity currentActivity() {
	    return activity;
	}
}
