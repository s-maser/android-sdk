package io.relayr.websocket;

import io.relayr.model.WebSocketConfig;

public class WebSocketFactory {

    public WebSocket createWebSocket(WebSocketConfig webSocketConfig) {
        return new WebSocket(webSocketConfig);
    }

}
