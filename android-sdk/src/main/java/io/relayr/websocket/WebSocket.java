package io.relayr.websocket;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.relayr.RelayrApp;
import rx.Observable;

abstract class WebSocket<T> {

    protected static final Object mLock = new Object();
    protected Map<String, List<WebSocketCallback>> mTopicCallbacks = new HashMap<>();

    protected static final int CONNECT_TIMEOUT = 10000;
    protected static final int SUBSCRIBE_TIMEOUT = 2000;
    protected static final int UNSUBSCRIBE_TIMEOUT = 1000;

    protected MqttAsyncClient mClient = null;

    public WebSocket() {
        SslUtil.init(RelayrApp.get());
    }

    public boolean isConnected() {
        return mClient != null && mClient.isConnected();
    }

    protected void publish(String topic, String payload) {
        try {
            final byte[] data = payload == null ? new byte[]{} : payload.getBytes();
            final IMqttDeliveryToken publishToken = mClient.publish(topic, data, 1, false);
            publishToken.waitForCompletion();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    abstract Observable<T> createClient(T object);

    abstract boolean subscribe(String topic, String channelId, final WebSocketCallback callback);

    abstract boolean unSubscribe(String topic);
}