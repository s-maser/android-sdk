package io.relayr.storage;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.Properties;

import static io.relayr.storage.RelayrProperties.*;

@RunWith(RobolectricTestRunner.class)
public class RelayrPropertiesTest {

    private Properties mProperties;

    @Before public void setUp() {
        mProperties = new Properties();
        mProperties.put(PROPERTIES_KEY_APP_ID, PROPERTIES_KEY_APP_ID);
        mProperties.put(PROPERTIES_KEY_CLIENT_SECRET, PROPERTIES_KEY_CLIENT_SECRET);
    }

    @Test public void loadPropertiesFile_assertNotNull() {
        Assert.assertNotNull(loadPropertiesFile(mProperties));
    }

    @Test public void loadPropertiesFile_assertPropertiesExist() {
        RelayrProperties properties = loadPropertiesFile(mProperties);
        Assert.assertEquals(properties.appId, PROPERTIES_KEY_APP_ID);
        Assert.assertEquals(properties.clientSecret, PROPERTIES_KEY_CLIENT_SECRET);
    }

}
