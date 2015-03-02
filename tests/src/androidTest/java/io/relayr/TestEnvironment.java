package io.relayr;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import dagger.ObjectGraph;
import io.relayr.api.TestModule;

@RunWith(RobolectricTestRunner.class)
public class TestEnvironment {

    public final String USER_ID = "shiny_id";
    public final String APP_NAME = "Test app";

    private CountDownLatch lock;

    @Before
    public void init() {
        lock = new CountDownLatch(1);

        MockitoAnnotations.initMocks(this);
    }

    public void inject() {
        ObjectGraph.create(new TestModule(Robolectric.application.getApplicationContext())).inject(this);
    }

    public void initSdk() {
        initSdk(true);
    }

    public void initSdk(boolean mock) {
        if (mock) RelayrSdk.initInMockMode(Robolectric.application.getApplicationContext());
        else RelayrSdk.init(Robolectric.application.getApplicationContext());
    }

    public void await() {
        try {
            lock.await(200, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void countDown() {
        lock.countDown();
    }
}
