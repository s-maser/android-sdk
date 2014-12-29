package io.relayr.websocket;

import io.relayr.model.WebSocketConfig;

public class WebSocketFactory {

    WebSocket createWebSocket(WebSocketConfig conf) {
        return new PubNubWebSocket(conf);
    }

    WebSocket createWebSocket() {
        return new MqttWebSocket();
    }
}
