package io.relayr.websocket;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.relayr.api.MockBackend;

@Module(
        complete = false,
        library = true
)
public class DebugWebSocketModule {

    @Provides @Singleton public WebSocketFactory provideWebSocketFactory(MockBackend loader) {
        return new MockWebSocketFactory(loader);
    }
}
