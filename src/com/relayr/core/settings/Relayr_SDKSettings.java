package com.relayr.core.settings;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.relayr.Relayr_Application;
import com.relayr.core.error.Relayr_Exception;

public final class Relayr_SDKSettings {

	private static String VERSION = "0.0.1";
	private static String clientId;
	private static String clientSecret;

	private static String propertiesFileName = "relayrsdk.properties";
	private static String clientIdTag = "clientId";
	private static String clientSecretTag = "clientSecret";

	public static String getVersion() {
		return VERSION;
	}

	public static void setClientId(String key) {
		clientId = key;
	}

	public static String getClientId() {
		return clientId;
	}

	public static String getClientSecret() {
		return clientSecret;
	}

	public static void setClientSecret(String clientSecret) {
		Relayr_SDKSettings.clientSecret = clientSecret;
	}

	public static boolean checkConfigValues() throws Exception {
		try {
			InputStream inputStream = Relayr_Application.currentActivity().getAssets().open(propertiesFileName);
			Properties relayrProperties = new Properties();
			relayrProperties.load(inputStream);
			inputStream.close();
			String clientId = relayrProperties.getProperty(clientIdTag);
			Relayr_SDKSettings.setClientId(clientId);
			String clientSecret = relayrProperties.getProperty(clientSecretTag);
			Relayr_SDKSettings.setClientSecret(clientSecret);
			return true;
		} catch (IOException e) {
			throw new Relayr_Exception("Can't find properties file");
		} catch (Exception e) {
			throw e;
		}
	}

}
