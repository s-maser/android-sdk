package io.relayr.model;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class LightColorProxTest {

    @Test public void toRgbTest() {
        LightColorProx.Color color = new LightColorProx.Color();
        color.red = 1000;
        color.green = 300;
        color.blue = 200;
        Assert.assertEquals(-36276, color.toRgb());
    }

}
