package com.relayr.core.settings;

import com.relayr.Relayr_Application;
import com.relayr.core.error.Relayr_Exception;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class Relayr_SDKSettings {

	private static String VERSION = "0.0.1";
	private static String clientId;
	private static String clientSecret;
    private static String appId;

	private static final String PROPERTIES_FILE_NAME = "relayrsdk.properties";
    private static final String PROPERTIES_KEY_CLIENT_ID = "clientId";
    private static final String PROPERTIES_KEY_CLIENT_SECRET = "clientSecret";
    private static final String PROPERTIES_KEY_APP_ID = "appId";

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

    private static void setAppId(String appId) {
        Relayr_SDKSettings.appId = appId;
    }

    public static String getAppId() {
        return appId;
    }

    public static boolean checkConfigValues() throws Exception {
		try {
			InputStream inputStream =
                    Relayr_Application.currentActivity().getAssets().open(PROPERTIES_FILE_NAME);
			Properties relayrProperties = new Properties();
			relayrProperties.load(inputStream);
			inputStream.close();
			String clientId = relayrProperties.getProperty(PROPERTIES_KEY_CLIENT_ID);
			Relayr_SDKSettings.setClientId(clientId);
			String clientSecret = relayrProperties.getProperty(PROPERTIES_KEY_CLIENT_SECRET);
			Relayr_SDKSettings.setClientSecret(clientSecret);
            String appId = relayrProperties.getProperty(PROPERTIES_KEY_APP_ID);
            Relayr_SDKSettings.setAppId(appId);
			return true;
		} catch (IOException e) {
			throw new Relayr_Exception("Can't find properties file");
		} catch (Exception e) {
			throw e;
		}
	}

}
