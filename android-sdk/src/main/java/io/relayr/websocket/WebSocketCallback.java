package io.relayr.websocket;

interface WebSocketCallback {

    void connectCallback(Object message);

    void disconnectCallback(Object message);

    void reconnectCallback(Object message);

    void successCallback(Object message);

    void errorCallback(Throwable error);

}
