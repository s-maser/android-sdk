package io.relayr.api;

import com.google.gson.reflect.TypeToken;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import javax.inject.Inject;

import io.relayr.TestEnvironment;
import io.relayr.model.App;
import io.relayr.model.Bookmark;
import io.relayr.model.Device;
import rx.Observer;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class MockBackendTest extends TestEnvironment {

    @Inject MockBackend backend;
    @Mock Observer<App> appObserver;

    @Before
    public void init() {
        super.init();
        inject();
    }

    @Test
    public void loadAppInfoFromFileTest() throws Exception {
        String load = backend.load(MockBackend.APP_INFO);

        assertThat(load).isNotNull();
        assertThat(load.contains(USER_ID));
        assertThat(load.contains(APP_NAME));
    }

    @Test
    public void loadAppInfoTest() throws Exception {
        App load = backend.load(new TypeToken<App>() {
        }, MockBackend.APP_INFO);

        assertThat(load).isNotNull();
        assertThat(load.id).isEqualTo(USER_ID);
        assertThat(load.name).isEqualTo(APP_NAME);
    }

    @Test
    public void loadBookmarkTest() throws Exception {
        Bookmark load = backend.load(new TypeToken<Bookmark>() {
        }, MockBackend.BOOKMARK_DEVICE);

        assertThat(load).isNotNull();
        assertThat(load.getCreatedAt()).isNotNull();
    }

    @Test(expected = Exception.class)
    public void loadWrongTypeDataTest_shouldThrowException() throws Exception {
        backend.load(new TypeToken<Device>() {
        }, MockBackend.BOOKMARKED_DEVICES);
    }

    @Test
    public void getWebSocketReadingsTest() {
        Object[] webSocketReadings = backend.getWebSocketReadings();

        assertThat(webSocketReadings).isNotNull();
        assertThat(webSocketReadings.length).isEqualTo(8);
        //TODO create a real test for new readings model
//        assertThat(webSocketReadings[0].readings.acceleration.y).isEqualTo(13.02f);
    }

    @Test
    public void createObservableTest() {
        backend.createObservable(new TypeToken<App>() {
        }, MockBackend.APP_INFO)
                .subscribe(appObserver);

        verify(appObserver, times(1)).onNext(any(App.class));
    }
}

