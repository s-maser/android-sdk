package io.relayr.api;

import com.google.gson.reflect.TypeToken;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import io.relayr.model.App;
import io.relayr.model.Device;
import io.relayr.model.Reading;
import rx.Observer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
public class MockBackendTest {

    private MockBackend mMockBackend;

    @Before
    public void init() {
        mMockBackend = new MockBackend(Robolectric.application);
    }

    @Test
    public void loadDataFromFileTest() throws Exception {
        String load = mMockBackend.load(MockBackend.APP_INFO);

        assertNotNull(load);
        assertTrue(load.contains("shiny_id"));
        assertTrue(load.contains("Test app"));
    }

    @Test
    public void loadDataTest() throws Exception {
        App load = mMockBackend.load(new TypeToken<App>() { }, MockBackend.APP_INFO);

        assertNotNull(load);
        assertEquals(load.id, "shiny_id");
        assertEquals(load.name, "Test app");
    }

    @Test(expected = Exception.class)
    public void loadWrongTypeDataTest_shouldThrowException() throws Exception {
        mMockBackend.load(new TypeToken<Device>() {
        }, MockBackend.PUBLIC_DEVICES_BOOKMARK);
    }

    @Test
    public void getWebSocketReadingsTest() {
        Reading[] webSocketReadings = mMockBackend.getWebSocketReadings();

        assertNotNull(webSocketReadings);
        assertEquals(8, webSocketReadings.length);
    }

    @Test
    public void createObservableTest() {
        Observer<App> subscriber = mock(Observer.class);

        mMockBackend.createObservable(new TypeToken<App>() {
        }, MockBackend.APP_INFO)
                .subscribe(subscriber);

        verify(subscriber, times(1)).onNext(any(App.class));
    }
}

