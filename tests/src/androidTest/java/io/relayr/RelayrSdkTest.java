package io.relayr;

import android.app.Activity;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class RelayrSdkTest {

    @Before public void init() {
        new RelayrSdk.Builder(Robolectric.application).build();
    }

    @Test public void getRelayrBleSdk_testStaticInjection() {
        Assert.assertNotNull(RelayrSdk.getRelayrBleSdk());
    }

    @Test public void getRelayrApi_testStaticInjection() {
        Assert.assertNotNull(RelayrSdk.getRelayrApi());
    }

    @Test public void getWebSocketClient_testStaticInjection() {
        Assert.assertNotNull(RelayrSdk.getWebSocketClient());
    }

    @Test public void isBleAvailable_shouldBeFalse() {
        Assert.assertFalse(RelayrSdk.isBleAvailable());
    }

    @Test public void isBleSupported_shouldBeFalse() {
        Assert.assertFalse(RelayrSdk.isBleSupported());
    }

    @Test public void getLoginEventListener_shouldNotBeNullIfTriedToLogIn() {
        Activity activity = Robolectric.buildActivity(Activity.class).create().get();
        RelayrSdk.logIn(activity).subscribe();
        Assert.assertNotNull(RelayrSdk.getLoginSubscriber());
    }

    @Test public void getVersion_shouldBeVersionName() {
        Assert.assertEquals(BuildConfig.VERSION_NAME, RelayrSdk.getVersion());
    }

}
