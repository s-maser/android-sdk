package io.relayr.api;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        complete = false,
        library = true
)
public class DebugApiModule {

    private final Context app;

    public DebugApiModule(Context context) {
        app = context;
    }

    @Provides @Singleton MockBackend provideMockBackend() {
        return new MockBackend(app);
    }

    @Provides @Singleton OauthApi provideOauthApi(MockBackend loader) {
        return new MockOauthApi(loader);
    }

    @Provides @Singleton RelayrApi provideRelayrApi(MockBackend loader) {
        return new MockRelayrApi(loader);
    }

    @Provides @Singleton SubscriptionApi provideSubscriptionApi(MockBackend loader) {
        return new MockSubscriptionApi(loader);
    }

    @Provides @Singleton CloudApi provideCloudApi() {
        return new MockCloudApi();
    }

    @Provides @Singleton StatusApi provideStatusApi(MockBackend loader) {
        return new MockStatusApi(loader);
    }

}
