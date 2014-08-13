package io.relayr.api;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.relayr.websocket.MockWebSocketFactory;
import io.relayr.websocket.WebSocketFactory;

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

    @Provides @Singleton public WebSocketFactory provideWebSocketFactory(MockBackend loader) {
        return new MockWebSocketFactory(loader);
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

}
