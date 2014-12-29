package io.relayr.websocket;

abstract class WebSocket<T> {

    abstract void subscribe(T channel, WebSocketCallback webSocketCallback);
}