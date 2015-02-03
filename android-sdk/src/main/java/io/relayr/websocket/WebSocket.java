package io.relayr.websocket;

import io.relayr.model.MqttChannel;

abstract class WebSocket<T> {

    abstract void subscribe(T channel, WebSocketCallback webSocketCallback);

    abstract void createClient(String clientId);

    abstract boolean unSubscribe(MqttChannel channel);
}