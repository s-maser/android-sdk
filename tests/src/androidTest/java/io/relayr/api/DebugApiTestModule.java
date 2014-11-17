package io.relayr.api;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        complete = false,
        library = true,
        overrides = true,
        injects = {
                MockStatusApiTest.class,
                MockBackendTest.class
        }
)
public class DebugApiTestModule {

    private final Context app;

    public DebugApiTestModule(Context context) {
        app = context;
    }

    @Provides
    @Singleton
    MockBackend provideMockBackend() {
        return new MockBackend(app);
    }

    @Provides
    RelayrApi provideMockRelayrApi(MockBackend mockBackend) {
        return new MockRelayrApi(mockBackend);
    }

    @Provides
    StatusApi provideStatusApi(MockBackend loader) {
        return new MockStatusApi(loader);
    }

}
