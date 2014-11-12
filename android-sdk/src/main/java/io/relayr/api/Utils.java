package io.relayr.api;

import android.os.Build;

import static io.relayr.BuildConfig.APPLICATION_ID;
import static io.relayr.BuildConfig.VERSION_NAME;

class Utils {

    static String getUserAgent() {
        return APPLICATION_ID + ".sdk.android/" + VERSION_NAME +
                " (android " + Build.VERSION.SDK_INT + ";" + getDeviceName() + ")";
    }

    private static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }

    private static String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

}
