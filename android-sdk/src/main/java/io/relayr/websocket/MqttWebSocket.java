package io.relayr.websocket;

import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.HashMap;
import java.util.Map;

import io.relayr.RelayrApp;
import io.relayr.model.MqttChannel;

class MqttWebSocket extends WebSocket<MqttChannel> {

    private static final int CONNECT_TIMEOUT = 2000;
    private static final int SUBSCRIBE_TIMEOUT = 2000;
    private static final int UNSUBSCRIBE_TIMEOUT = 2000;

    private Map<String, MqttAsyncClient> mClients = new HashMap<>();

    public MqttWebSocket() {
        SslUtil.init(RelayrApp.get());
    }

    public boolean createClient(String clientId) {
        if (clientId == null || clientId.isEmpty()) {
            Log.e("WebSocket", "ClientId '" + clientId + "' not valid!");
            return false;
        }

        if (clientExist(clientId)) {
            Log.i("WebSocket", "Client " + clientId + " already exist.");
            return true;
        }

        return addClient(clientId) != null;
    }

    @Override
    public boolean unSubscribe(MqttChannel channel) {
        if (channel == null) {
            Log.e("WebSocket", "MqttChannel can't be null!");
            return false;
        }

        if (mClients == null || mClients.isEmpty()) {
            Log.w("WebSocket", "No existing MQTT clients.");
            return false;
        }

        final String clientId = channel.getCredentials().getClientId();
        if (!clientExist(clientId)) {
            Log.w("WebSocket", "MqttClient for clientId " + clientId + " not found!");
            return false;
        }

        try {
            final MqttAsyncClient client = mClients.get(clientId);
            final IMqttToken unSubscribeToken = client.unsubscribe(channel.getCredentials().getTopic());
            unSubscribeToken.waitForCompletion(UNSUBSCRIBE_TIMEOUT);
            mClients.remove(clientId);
            return true;
        } catch (MqttException e) {
            e.printStackTrace();
            return false;
        }
    }

    void subscribe(MqttChannel channel, final WebSocketCallback callback) {
        if (callback == null) {
            Log.e("WebSocket", "Argument WebSocketCallback can not be null!");
            return;
        }
        if (channel == null) {
            callback.errorCallback(new IllegalArgumentException("MqttChannel can't be null!"));
            return;
        }

        MqttAsyncClient client = addClient(channel.getCredentials().getClientId());
        if (client == null) {
            callback.disconnectCallback(new IllegalArgumentException("MqttClient creation failed!"));
            return;
        }

        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                callback.disconnectCallback(cause);
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) {
                callback.successCallback(message);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
            }
        });

        try {
            final IMqttToken connectToken = client.connect(SslUtil.instance().getConnectOptions
                    (channel.getCredentials()));
            connectToken.waitForCompletion(CONNECT_TIMEOUT);
            callback.connectCallback("Connected to channel " + channel.getChannelId());

            try {
                final IMqttToken subscribeToken = client.subscribe(channel.getCredentials().getTopic(), 1);
                subscribeToken.waitForCompletion(SUBSCRIBE_TIMEOUT);
                callback.connectCallback("Subscribed to channel " + channel.getChannelId());
            } catch (MqttException e) {
                callback.disconnectCallback(e);
                e.printStackTrace();
            }
        } catch (MqttException e) {
            callback.disconnectCallback(e);
            e.printStackTrace();
        }
    }

    private boolean clientExist(String clientId) {
        final MqttAsyncClient client = mClients.get(clientId);
        return client != null;
    }

    private MqttAsyncClient addClient(String clientId) {
        if (clientExist(clientId)) return mClients.get(clientId);

        try {
            mClients.put(clientId, new MqttAsyncClient(SslUtil.instance().getBroker(), clientId, null));
        } catch (MqttException e) {
            Log.e("WebSocket", "Client with clientId " + clientId + " can't be created!");
            e.printStackTrace();
            return null;
        }

        return mClients.get(clientId);
    }
}
