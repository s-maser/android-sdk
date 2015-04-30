package io.relayr.websocket;

import android.util.Log;
import android.util.Pair;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import io.relayr.model.Transmitter;
import rx.Observable;
import rx.Subscriber;

class OnBoardWebSocket extends WebSocket<Transmitter> {

    private final String TAG = "OnBoardWebSocket";

    @Override
    public Observable<Transmitter> createClient(final Transmitter transmitter) {
        synchronized (mLock) {
            return Observable.create(new Observable.OnSubscribe<Transmitter>() {
                @Override
                public void call(Subscriber<? super Transmitter> subscriber) {
                    if (mClient != null && mClient.isConnected()) {
                        subscriber.onNext(transmitter);
                        return;
                    }

                    if (transmitter == null) {
                        subscriber.onError(new Throwable("MqttChannel data can't be null"));
                        return;
                    }

                    if (createMqttClient("Android-OB-WB2")) {
                        try {
                            if (!mClient.isConnected()) {
                                final IMqttToken connectToken = mClient.connect(SslUtil.instance().
                                        getConnectOptions(transmitter.id, transmitter.secret));
                                connectToken.waitForCompletion(CONNECT_TIMEOUT);
                                subscriber.onNext(transmitter);
                            }
                        } catch (MqttException e) {
                            Log.d(TAG, "Failed to connect.");
                            subscriber.onError(e);
                        }
                    } else {
                        Log.d(TAG, "Client not created.");
                        subscriber.onError(new Throwable("Client not created!"));
                    }
                }
            });
        }
    }

    @Override
    public boolean subscribe(String topic, String channelId, final WebSocketCallback callback) {
        if (callback == null) {
            Log.e(TAG, "Argument WebSocketCallback can not be null!");
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
        } catch (MqttException e) {
            callback.disconnectCallback(e);
            return false;
        }

        return true;
    }

    @Override
    public boolean unSubscribe(String topic) {
        if (topic == null) {
            Log.e(TAG, "Topic can't be null!");
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

    boolean createMqttClient(String clientId) {
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

                    Log.d(TAG, "Connection lost.");
                    cause.printStackTrace();
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) {
                    if (mTopicCallbacks == null || mTopicCallbacks.isEmpty()) return;

                    if (mTopicCallbacks.get(topic) == null) {
                        final String top = topic.substring(0, topic.lastIndexOf("/")) + "/#";
                        final String data = topic.substring(topic.lastIndexOf("/") + 1, topic.length());
                        for (Map.Entry<String, List<WebSocketCallback>> entry : mTopicCallbacks.entrySet())
                            if (entry.getKey().equals(top))
                                for (WebSocketCallback socketCallback : entry.getValue())
                                    socketCallback.successCallback(new Pair<>(data, message.toString()));
                        return;
                    }

                    for (WebSocketCallback socketCallback : mTopicCallbacks.get(topic))
                        socketCallback.successCallback(message);
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                }
            });

            return true;
        } catch (MqttException e) {
            Log.d(TAG, "Error creating client.");
            if (mTopicCallbacks == null || mTopicCallbacks.isEmpty()) return false;
            for (List<WebSocketCallback> callbacks : mTopicCallbacks.values())
                for (WebSocketCallback socketCallback : callbacks)
                    socketCallback.disconnectCallback(e);

            return false;
        }
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
