package io.relayr.websocket;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        complete = false,
        library = true
)
public class WebSocketModule {

    @Provides @Singleton public WebSocketFactory provideWebSocketFactory() {
        return new WebSocketFactory();
    }

}
