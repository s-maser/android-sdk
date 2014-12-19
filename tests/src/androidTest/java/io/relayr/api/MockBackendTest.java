package io.relayr.api;

import com.google.gson.reflect.TypeToken;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import javax.inject.Inject;

import dagger.ObjectGraph;
import io.relayr.model.App;
import io.relayr.model.Bookmark;
import io.relayr.model.Device;
import io.relayr.model.Reading;
import rx.Observer;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
public class MockBackendTest {

    @Inject MockBackend backend;
    @Mock Observer<App> appObserver;

    @Before
    public void init() {
        ObjectGraph.create(new TestModule(Robolectric.application)).inject(this);
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void loadAppInfoFromFileTest() throws Exception {
        String load = backend.load(MockBackend.APP_INFO);

        assertThat(load).isNotNull();
        assertThat(load.contains("shiny_id"));
        assertThat(load.contains("Test app"));
    }

    @Test
    public void loadAppInfoTest() throws Exception {
        App load = backend.load(new TypeToken<App>() {
        }, MockBackend.APP_INFO);

        assertThat(load).isNotNull();
        assertThat(load.id).isEqualTo("shiny_id");
        assertThat(load.name).isEqualTo("Test app");
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
        Reading[] webSocketReadings = backend.getWebSocketReadings();

        assertThat(webSocketReadings).isNotNull();
        assertThat(webSocketReadings.length).isEqualTo(8);
        assertThat(webSocketReadings[0].accel.y).isEqualTo(13.02f);
    }

    @Test
    public void createObservableTest() {
        backend.createObservable(new TypeToken<App>() {
        }, MockBackend.APP_INFO)
                .subscribe(appObserver);

        verify(appObserver, times(1)).onNext(any(App.class));
    }
}

