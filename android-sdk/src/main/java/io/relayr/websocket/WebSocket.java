package io.relayr.websocket;

abstract class WebSocket<T> {

    abstract void subscribe(T channel, WebSocketCallback webSocketCallback);

    abstract void createClient(String clientId);

    abstract boolean unSubscribe(String topic);
}