package io.relayr;

import android.app.Activity;
import android.content.Context;

public class RelayrApp {

    private static Context sApplicationContext;
	private static Activity activity;

    public static void init(Context context) {
        sApplicationContext = context.getApplicationContext();
    }

    public static Context get() {
        return sApplicationContext;
    }

	public static void setCurrentActivity(Activity currentActivity) {
	    activity = currentActivity;
	}

}
