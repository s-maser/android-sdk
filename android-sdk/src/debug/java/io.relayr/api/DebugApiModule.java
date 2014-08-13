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

    @Provides @Singleton io.relayr.api.MockBackend provideMockBackend() {
        return new io.relayr.api.MockBackend(app);
    }

    /*@Provides @Singleton public WebSocketFactory provideWebSocketFactory(MockBackend loader) {
        return new MockWebSocketFactory(loader);
    }*/

    @Provides @Singleton OauthApi provideOauthApi(io.relayr.api.MockBackend loader) {
        return new io.relayr.api.MockOauthApi(loader);
    }

    @Provides @Singleton RelayrApi provideRelayrApi(io.relayr.api.MockBackend loader) {
        return new io.relayr.api.MockRelayrApi(loader);
    }

}
