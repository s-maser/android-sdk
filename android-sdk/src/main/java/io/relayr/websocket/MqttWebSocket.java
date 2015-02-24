package io.relayr.websocket;

import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.relayr.RelayrApp;
import io.relayr.model.MqttChannel;
import rx.Subscriber;

class MqttWebSocket extends WebSocket<MqttChannel> {

    private static final Object mLock = new Object();

    private static final int CONNECT_TIMEOUT = 2000;
    private static final int SUBSCRIBE_TIMEOUT = 2000;
    private static final int UNSUBSCRIBE_TIMEOUT = 1000;

    private MqttAsyncClient mClient = null;
    private Map<String, List<WebSocketCallback>> mTopicCallbacks = new HashMap<>();

    public MqttWebSocket() {
        SslUtil.init(RelayrApp.get());
    }

    @Override
    public void createClient(final MqttChannel channel, Subscriber<Void> subscriber) {
        synchronized (mLock) {
            if (mClient != null && mClient.isConnected()) {
                subscriber.onNext(null);
                return;
            }

            if (channel == null) {
                subscriber.onError(new Throwable("MqttChannel data can't be null"));
                return;
            }

            if (createMqttClient(channel.getCredentials().getClientId())) {
                try {
                    connect(channel.getCredentials());
                    subscriber.onNext(null);
                } catch (MqttException e) {
                    subscriber.onError(e);
                }
            } else {
                subscriber.onError(new Throwable("Client not created!"));
            }
        }
    }

    @Override
    public boolean unSubscribe(MqttChannel channel) {
        if (channel == null) {
            Log.e("WebSocket", "MqttChannel can't be null!");
            return false;
        }

        try {
            final IMqttToken unSubscribeToken = mClient.unsubscribe(channel.getCredentials().getTopic());
            unSubscribeToken.waitForCompletion(UNSUBSCRIBE_TIMEOUT);
            mTopicCallbacks.remove(channel.getCredentials().getTopic());
            return true;
        } catch (MqttException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean createMqttClient(String clientId) {
        if (mClient != null) return true;

        try {
            mClient = new MqttAsyncClient(SslUtil.instance().getBroker(), clientId, null);
            mClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    if (mTopicCallbacks == null || mTopicCallbacks.isEmpty()) return;

                    for (List<WebSocketCallback> callbacks : mTopicCallbacks.values())
                        for (WebSocketCallback socketCallback : callbacks)
                            socketCallback.disconnectCallback(cause);
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) {
                    if (mTopicCallbacks == null || mTopicCallbacks.isEmpty()) return;

                    final List<WebSocketCallback> callbacks = mTopicCallbacks.get(topic);
                    for (WebSocketCallback socketCallback : callbacks)
                        socketCallback.successCallback(message);
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                }
            });

            return true;
        } catch (MqttException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean subscribe(final MqttChannel channel, final WebSocketCallback callback) {
        if (callback == null) {
            Log.e("WebSocket", "Argument WebSocketCallback can not be null!");
            return false;
        }

        if (channel == null) {
            callback.errorCallback(new IllegalArgumentException("MqttChannel can't be null!"));
            return false;
        }

        final String topic = channel.getCredentials().getTopic();
        if (mTopicCallbacks.containsKey(topic)) {
            addCallback(topic, callback);
            return true;
        }

        try {
            subscribe(topic);
            addCallback(topic, callback);
            callback.connectCallback("Subscribed to " + channel.getChannelId());
        } catch (MqttException e) {
            callback.disconnectCallback(e);
            return false;
        }

        return true;
    }

    private void addCallback(String topic, WebSocketCallback callback) {
        final List<WebSocketCallback> callbacks = mTopicCallbacks.get(topic);
        if (callbacks == null)
            mTopicCallbacks.put(topic, Arrays.asList(callback));
        else
            callbacks.add(callback);
    }

    private void connect(MqttChannel.MqttCredentials credentials) throws MqttException {
        try {
            if (!mClient.isConnected()) {
                final IMqttToken connectToken = mClient.connect(SslUtil.instance().getConnectOptions(credentials));
                connectToken.waitForCompletion(CONNECT_TIMEOUT);
            }
        } catch (MqttException e) {
            SslUtil.instance().refreshCertificate();
            throw e;
        }
    }

    private void subscribe(String topic) throws MqttException {
        List<String> topics = new ArrayList<>();
        topics.add(topic);
        topics.addAll(mTopicCallbacks.keySet());

        int[] qos = new int[topics.size()];
        Arrays.fill(qos, 1);

        final IMqttToken subscribeToken = mClient.subscribe(topics.toArray(new String[topics.size()]), qos);

        subscribeToken.waitForCompletion(SUBSCRIBE_TIMEOUT);
    }
}
