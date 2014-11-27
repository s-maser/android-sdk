package io.relayr.storage;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import io.relayr.RelayrApp;

public final class RelayrProperties {

    private static final String PROPERTIES_FILE_NAME = "relayrsdk.properties";
    static final String DEFAULT_REDIRECT_URI = "http://localhost";
    static final String PROPERTIES_KEY_CLIENT_SECRET = "clientSecret";
    static final String PROPERTIES_KEY_APP_ID = "appId";
    static final String PROPERTIES_KEY_REDIRECT_URI = "redirectUri";

    private static RelayrProperties mRelayrProperties = null;

    public final String clientSecret;
    public final String appId;
    public final String redirectUri;

    private RelayrProperties(String clientSecret, String appId, String redirectUri) {
        this.clientSecret = clientSecret;
        this.appId = appId;
        this.redirectUri = redirectUri;
    }

    public static RelayrProperties get() {
        if (mRelayrProperties == null) {
            synchronized (RelayrProperties.class) {
                if (mRelayrProperties == null) {
                    mRelayrProperties = loadPropertiesFile(provideProperties());
                }
            }
        }
        return mRelayrProperties;
    }

    private static Properties provideProperties() {
        Properties properties = new Properties();

        try {
            InputStream inputStream = RelayrApp.get().getAssets().open(PROPERTIES_FILE_NAME);
            properties.load(inputStream);
            inputStream.close();
        } catch (IOException e) {
            //throw new Relayr_Exception("Can't find properties file");
        }
        return properties;
    }

    static RelayrProperties loadPropertiesFile(Properties properties) {
        String clientSecret = getProperty(properties.getProperty(PROPERTIES_KEY_CLIENT_SECRET));
        String appId = getProperty(properties.getProperty(PROPERTIES_KEY_APP_ID));
        String redirectUri = getProperty(properties.getProperty(PROPERTIES_KEY_REDIRECT_URI));
        return new RelayrProperties(clientSecret, appId,
                redirectUri.length() != 0? redirectUri: DEFAULT_REDIRECT_URI);
	}

    private static String getProperty(String property) {
        return property == null ? "": property;
    }

}
