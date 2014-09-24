package io.relayr;

import android.app.Activity;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class RelayrSdkTest {

    @Test public void getRelayrBleSdk_testStaticInjection() {
        RelayrSdk.init(Robolectric.application);
        Assert.assertNotNull(RelayrSdk.getRelayrBleSdk());
    }

    @Test public void getRelayrApi_testStaticInjection() {
        RelayrSdk.init(Robolectric.application);
        Assert.assertNotNull(RelayrSdk.getRelayrApi());
    }

    @Test public void getWebSocketClient_testStaticInjection() {
        RelayrSdk.init(Robolectric.application);
        Assert.assertNotNull(RelayrSdk.getWebSocketClient());
    }

    @Test public void isBleAvailable_shouldBeFalse() {
        RelayrSdk.init(Robolectric.application);
        Assert.assertFalse(RelayrSdk.isBleAvailable());
    }

    @Test public void isBleSupported_shouldBeFalse() {
        RelayrSdk.init(Robolectric.application);
        Assert.assertFalse(RelayrSdk.isBleSupported());
    }

    @Test public void getLoginEventListener_shouldBeNullIfNotTriedToLogIn() {
        RelayrSdk.init(Robolectric.application);
        Activity activity = Robolectric.buildActivity(Activity.class).create().get();
        RelayrSdk.logIn(activity, null);
        Assert.assertNull(RelayrSdk.getLoginEventListener());
    }

    @Test public void getLoginEventListener_shouldNotBeNullIfTriedToLogIn() {
        RelayrSdk.init(Robolectric.application);
        Activity activity = Robolectric.buildActivity(Activity.class).create().get();
        RelayrSdk.logIn(activity, Mockito.mock(LoginEventListener.class));
        Assert.assertNotNull(RelayrSdk.getLoginEventListener());
    }

    @Test public void getVersion_shouldBeVersionName() {
        RelayrSdk.init(Robolectric.application);
        Assert.assertEquals(BuildConfig.VERSION_NAME, RelayrSdk.getVersion());
    }

}
