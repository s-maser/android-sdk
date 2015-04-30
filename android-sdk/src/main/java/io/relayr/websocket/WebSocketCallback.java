package io.relayr.websocket;

public interface WebSocketCallback {

    void connectCallback(Object message);

    void disconnectCallback(Object message);

    void successCallback(Object message);

    void errorCallback(Throwable error);

}
