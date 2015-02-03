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

    private MqttAsyncClient mClient;
    private Map<String, MqttChannel> deviceChannels = new HashMap<>();

    public MqttWebSocket() {
        Log.d("MqttWebSocket", "Creating WebSocket");
        SslUtil.init(RelayrApp.get());
    }

    public void createClient(String clientId) {
        Log.d("MqttWebSocket", "ClientId: " + clientId);

        if (clientId == null || clientId.isEmpty()) {
            Log.e("MqttWebSocket", "ClientId '" + clientId + "' not valid!");
            return;
        }

        if (mClient != null && clientId.equals(mClient.getClientId())) return;

        try {
            mClient = new MqttAsyncClient(SslUtil.instance().getBroker(), clientId, null);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean unSubscribe(String topic) {
        try {
            final IMqttToken unsubscribeToken = mClient.unsubscribe(topic);
            unsubscribeToken.waitForCompletion();
            return true;
        } catch (MqttException e) {
            e.printStackTrace();
            return false;
        }
    }

    void subscribe(MqttChannel channel, final WebSocketCallback webSocketCallback) {
        mClient.setCallback(new MqttCallback() {
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
            final IMqttToken connectToken = mClient.connect(SslUtil.instance().getConnectOptions
                    (channel.getCredentials()));
            connectToken.waitForCompletion();
            webSocketCallback.connectCallback("Connected");

            try {
                final IMqttToken subscribeToken = mClient.subscribe(channel.getCredentials().getTopic(), 1);
                subscribeToken.waitForCompletion();
                webSocketCallback.connectCallback("Subscribed");
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
