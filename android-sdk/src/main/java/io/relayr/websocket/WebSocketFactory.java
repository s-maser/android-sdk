package io.relayr.websocket;

public class WebSocketFactory {

    WebSocket createWebSocket() {
        return new MqttWebSocket();
    }

    WebSocket createOnBoardingWebSocket() {
        return new OnBoardWebSocket();
    }
}
