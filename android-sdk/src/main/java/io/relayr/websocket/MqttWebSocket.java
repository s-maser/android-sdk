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

import io.relayr.model.MqttChannel;
import rx.Observable;
import rx.Subscriber;

class MqttWebSocket extends WebSocket<MqttChannel> {

    private Map<String, List<WebSocketCallback>> mTopicCallbacks = new HashMap<>();

    @Override
    public Observable<MqttChannel> createClient(final MqttChannel channel) {
        return Observable.create(new Observable.OnSubscribe<MqttChannel>() {
            @Override
            public void call(Subscriber<? super MqttChannel> subscriber) {
                synchronized (mLock) {
                    if (mClient != null && mClient.isConnected()) {
                        subscriber.onNext(channel);
                        return;
                    }

                    if (channel == null) {
                        subscriber.onError(new Throwable("MqttChannel data can't be null"));
                        return;
                    }

                    if (createMqttClient(channel.getCredentials().getClientId())) {
                        try {
                            connect(channel.getCredentials().getUser(), channel.getCredentials().getPassword());
                            subscriber.onNext(channel);
                        } catch (MqttException e) {
                            subscriber.onError(e);
                        }
                    } else {
                        subscriber.onError(new Throwable("Client not created!"));
                    }
                }
            }
        });
    }

    @Override
    public boolean unSubscribe(String topic) {
        if (topic == null) {
            Log.d("MqttWebSocket", "Topic can't be null!");
            return false;
        }

        try {
            mTopicCallbacks.remove(topic);
            final IMqttToken unSubscribeToken = mClient.unsubscribe(topic);
            unSubscribeToken.waitForCompletion(UNSUBSCRIBE_TIMEOUT);
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

                    for (WebSocketCallback socketCallback : mTopicCallbacks.get(topic))
                        socketCallback.successCallback(message);
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                }
            });

            return true;
        } catch (MqttException e) {
            if (mTopicCallbacks == null || mTopicCallbacks.isEmpty()) return false;
            for (List<WebSocketCallback> callbacks : mTopicCallbacks.values())
                for (WebSocketCallback socketCallback : callbacks)
                    socketCallback.disconnectCallback(e);

            return false;
        }
    }

    private void connect(String username, String password) throws MqttException {
        if (!mClient.isConnected()) {
            final IMqttToken connectToken = mClient.connect(SslUtil.instance().getConnectOptions(username, password));
            connectToken.waitForCompletion(CONNECT_TIMEOUT);
        }
    }

    @Override
    public boolean subscribe(String topic, String channelId, final WebSocketCallback callback) {
        if (callback == null) {
            Log.e("MqttWebSocket", "Argument WebSocketCallback can not be null!");
            return false;
        }

        if (topic == null) {
            callback.errorCallback(new IllegalArgumentException("Topic can't be null!"));
            return false;
        }

        if (mTopicCallbacks.containsKey(topic)) {
            addCallback(topic, callback);
            return true;
        }

        try {
            subscribe(topic);
            addCallback(topic, callback);
            callback.connectCallback("Subscribed to " + channelId);
        } catch (MqttException e) {
            callback.disconnectCallback(e);
            return false;
        }

        return true;
    }

    private void addCallback(String topic, WebSocketCallback callback) {
        if (mTopicCallbacks.get(topic) == null)
            mTopicCallbacks.put(topic, new ArrayList<>(Arrays.asList(callback)));
        else
            mTopicCallbacks.get(topic).add(callback);
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
