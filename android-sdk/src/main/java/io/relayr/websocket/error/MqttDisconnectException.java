package io.relayr.websocket.error;

public class MqttDisconnectException extends Throwable {

    public MqttDisconnectException(String detailMessage) {
        super(detailMessage);
    }

    public MqttDisconnectException() {
        super();
    }
}

