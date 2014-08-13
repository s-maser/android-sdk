package io.relayr;

import android.app.Activity;
import android.content.Context;

import javax.inject.Inject;

import io.relayr.activity.LoginActivity;
import io.relayr.api.RelayrApi;
import io.relayr.ble.BleUtils;
import io.relayr.ble.RelayrBleSdk;
import io.relayr.storage.DataStorage;
import io.relayr.storage.RelayrProperties;

public class RelayrSdk {

    @Inject static RelayrApi mRelayrApi;

	private static LoginEventListener loginEventListener;

    /** Should be called when the {@link android.app.Application} is created */
    public static void init(Context context) {
        RelayrApp.init(context);
    }

    /** Returns the version of the sdk */
	public static String getVersion() {
        return RelayrProperties.VERSION;
	}

    /** Returns the handler of the api */
    public static RelayrApi getRelayrApi() {
        return mRelayrApi;
    }

    /** Launches the log in Activity that will log the user into the app */
	public static void logIn(Activity currentActivity, LoginEventListener listener) {
        loginEventListener = listener;
        LoginActivity.startActivity(currentActivity);
	}

    /** Checks whether the user is logged in in the sdk or not */
	public static boolean isUserLoggedIn() {
		return DataStorage.isUserLoggedIn();
	}

    /** Logs the user out of the sdk */
	public static void logOut() {
		DataStorage.logOut();
	}

    /** {@link io.relayr.ble.RelayrBleSdk#newInstance()} */
     public static RelayrBleSdk getRelayrBleSdk() {
        return RelayrBleSdk.newInstance();
    }

    /** Checks whether ble is supported or not. It should be called before using
     * {@link #getRelayrBleSdk} */
    public static boolean isBleSupported() {
        return BleUtils.isBleSupported();
    }

    /** Checks whether ble is on or off. Bluetooth can be activated by calling
     * {@link #promptUserToActivateBluetooth}. It should be called before using
     * {@link #getRelayrBleSdk} */
    public static boolean isBleAvailable() {
        return BleUtils.isBleAvailable();
    }

    /** Launches an activity to ask the user to activate the bluetooth. It won't do anything if
     * bluetooth is not supported {@link #isBleSupported} */
    public static void promptUserToActivateBluetooth(Activity activity) {
        if (isBleSupported()) BleUtils.promptUserToActivateBluetooth(activity);
    }

	public static LoginEventListener getLoginEventListener() {
		return loginEventListener;
	}
}
