package com.relayr.core.settings;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.relayr.Relayr_Application;
import com.relayr.core.error.Relayr_Exception;

public final class Relayr_SDKSettings {

	private static String VERSION = "0.0.1";
	private static String appKey;
	private static String token;
	private static String propertiesFileName = "relayrsdk.properties";
	private static String appKeyTag = "appKey";

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

	public static boolean checkConfigValues() throws Exception {
		try {
			InputStream inputStream = Relayr_Application.currentActivity().getAssets().open(propertiesFileName);
			Properties relayrProperties = new Properties();
			relayrProperties.load(inputStream);
			inputStream.close();
			String appKey = relayrProperties.getProperty(appKeyTag);
			Relayr_SDKSettings.setAppKey(appKey);
			return true;
		} catch (IOException e) {
			throw new Relayr_Exception("Can't find properties file");
		} catch (Exception e) {
			throw e;
		}
	}

}
