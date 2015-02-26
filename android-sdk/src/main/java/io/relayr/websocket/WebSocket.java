package io.relayr.websocket;

import io.relayr.model.MqttChannel;
import rx.Subscriber;

abstract class WebSocket<T> {

    abstract boolean subscribe(T channel, WebSocketCallback webSocketCallback);

    abstract void createClient(MqttChannel channel, Subscriber<Void> subscriber);

    abstract boolean unSubscribe(MqttChannel channel);
}