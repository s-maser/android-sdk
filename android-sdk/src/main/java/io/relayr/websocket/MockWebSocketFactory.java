package io.relayr.websocket;

import javax.inject.Inject;

import io.relayr.api.MockBackend;
import io.relayr.model.WebSocketConfig;

public class MockWebSocketFactory extends WebSocketFactory {

    private final MockBackend mMockBackend;

    @Inject
    public MockWebSocketFactory(MockBackend mockBackend) {
        mMockBackend = mockBackend;
    }

    public WebSocket createWebSocket(WebSocketConfig webSocketConfig) {
        return new MockWebSocket(webSocketConfig, mMockBackend);
    }

}
