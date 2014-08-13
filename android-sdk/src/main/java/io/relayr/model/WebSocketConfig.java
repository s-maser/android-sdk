package io.relayr.model;

public class WebSocketConfig {

    public final String authKey;
    public final String cipherKey;
    public final String channel;
    public final String subscribeKey;

    public WebSocketConfig(String authKey, String cipherKey, String channel, String subscribeKey) {
        this.authKey = authKey;
        this.cipherKey = cipherKey;
        this.channel = channel;
        this.subscribeKey = subscribeKey;
    }
}
