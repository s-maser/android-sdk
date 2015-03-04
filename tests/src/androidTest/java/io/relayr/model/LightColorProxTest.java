package io.relayr.model;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class LightColorProxTest {

    @Test public void toRgbTest() {
        LightColorProx.Color color = new LightColorProx.Color(1000, 300, 200);
        Assert.assertEquals(-36276, color.toRgb());
    }

}
