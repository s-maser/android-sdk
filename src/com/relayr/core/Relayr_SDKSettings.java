package com.relayr.core;

public final class Relayr_SDKSettings {

	private static String VERSION = "0.0.1";
	private static String appKey;
	private static String token;

	public static String getVersion() {
		return VERSION;
	}

	public static void setAppKey(String key) {
		appKey = key;
	}

	public static String getAppKey() {
		return appKey;
	}

	public static void setToken(String newToken) {
		token = newToken;
	}

	public static String getUserToken() {
		return token;
	}


}
