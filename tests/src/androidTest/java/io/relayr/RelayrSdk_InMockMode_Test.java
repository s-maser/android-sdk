package io.relayr;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class RelayrSdk_InMockMode_Test {

    @Test public void getRelayrBleSdk_testStaticInjection() {
        RelayrSdk.initInMockMode(Robolectric.application);
        Assert.assertNotNull(RelayrSdk.getRelayrBleSdk());
    }

    @Test public void getRelayrApi_testStaticInjection() {
        RelayrSdk.initInMockMode(Robolectric.application);
        Assert.assertNotNull(RelayrSdk.getRelayrApi());
    }

    @Test public void getWebSocketClient_testStaticInjection() {
        RelayrSdk.initInMockMode(Robolectric.application);
        Assert.assertNotNull(RelayrSdk.getWebSocketClient());
    }


}
