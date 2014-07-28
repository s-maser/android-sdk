package io.relayr;

import android.os.Build;

public class Relayr_Commons {

	public static boolean isSDK18() {
		return android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2;
	}

}
