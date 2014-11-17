package io.relayr.api;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import javax.inject.Inject;

import dagger.ObjectGraph;
import io.relayr.model.Status;
import rx.Observer;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
public class MockStatusApiTest {

    @Inject
    StatusApi statusApi;

    @Captor
    private ArgumentCaptor<Status> statusCaptor;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        ObjectGraph.create(new TestModule(Robolectric.application)).inject(this);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void getServerStatusTest() throws Exception {
        Observer<Status> subscriber = mock(Observer.class);

        statusApi.getServerStatus().subscribe(subscriber);

        verify(subscriber).onNext(statusCaptor.capture());

        assertThat(statusCaptor.getValue()).isNotNull();
    }
}
