package io.relayr.websocket;

import io.relayr.model.WebSocketConfig;

class WebSocketFactory {

    MqttWebSocket createWebSocket(WebSocketConfig webSocketConfig) {
        return new MqttWebSocket(webSocketConfig);
    }

}
