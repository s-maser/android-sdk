package io.relayr.websocket;

import io.relayr.model.WebSocketConfig;

public class WebSocketFactory {

    WebSocket createWebSocket() {
        return new MqttWebSocket();
    }
}
