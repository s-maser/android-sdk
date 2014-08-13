package io.relayr.websocket;

import javax.inject.Inject;

import io.relayr.api.MockBackend;
import io.relayr.model.WebSocketConfig;

class MockWebSocketFactory extends WebSocketFactory {

    private final MockBackend mMockBackend;

    @Inject
    MockWebSocketFactory(MockBackend mockBackend) {
        mMockBackend = mockBackend;
    }

    WebSocket createWebSocket(WebSocketConfig webSocketConfig) {
        return new MockWebSocket(webSocketConfig, mMockBackend);
    }

}
