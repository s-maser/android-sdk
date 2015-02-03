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

    private Map<String, MqttAsyncClient> mClients = new HashMap<>();

    public MqttWebSocket() {
        Log.i("WS", "Creating WebSocket");
        SslUtil.init(RelayrApp.get());
    }

    public void createClient(String clientId) {
        if (clientId == null || clientId.isEmpty()) {
            Log.e("WS", "ClientId '" + clientId + "' not valid!");
            return;
        }

        if (mClients.containsKey(clientId)) {
            Log.i("WS", "Client " + clientId + " already exist.");
            return;
        }
        try {
            mClients.put(clientId, new MqttAsyncClient(SslUtil.instance().getBroker(), clientId, null));
            Log.i("WS", "Client for " + clientId + " created.");
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean unSubscribe(MqttChannel channel) {
        final MqttAsyncClient client = mClients.get(channel.getCredentials().getClientId());
        try {
            client.unsubscribe(channel.getCredentials().getTopic());
            return true;
        } catch (MqttException e) {
            e.printStackTrace();
            return false;
        }
    }

    void subscribe(MqttChannel channel, final WebSocketCallback webSocketCallback) {
        final MqttAsyncClient client = mClients.get(channel.getCredentials().getClientId());
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                webSocketCallback.disconnectCallback(cause);
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                webSocketCallback.successCallback(message);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
            }
        });

        try {
            final IMqttToken connectToken = client.connect(SslUtil.instance().getConnectOptions
                    (channel.getCredentials()));
            connectToken.waitForCompletion();
            webSocketCallback.connectCallback("Connected to channel " + channel.getChannelId());

            try {
                final IMqttToken subscribeToken = client.subscribe(channel.getCredentials().getTopic(), 1);
                subscribeToken.waitForCompletion();
                webSocketCallback.connectCallback("Subscribed to channel " + channel.getChannelId());
            } catch (MqttException e) {
                webSocketCallback.errorCallback(e);
                e.printStackTrace();
            }
        } catch (MqttException e) {
            webSocketCallback.errorCallback(e);
            e.printStackTrace();
        }
    }
}
