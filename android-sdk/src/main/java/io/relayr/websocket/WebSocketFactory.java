package io.relayr.websocket;

import io.relayr.model.WebSocketConfig;

class WebSocketFactory {

    WebSocket createWebSocket(WebSocketConfig webSocketConfig) {
        return new WebSocket(webSocketConfig);
    }

}
