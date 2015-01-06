package io.relayr.websocket;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import io.relayr.RelayrApp;
import io.relayr.model.MqttChannel;

class MqttWebSocket extends WebSocket<MqttChannel> {

    private IMqttClient mClient;

    public MqttWebSocket() {
        SslUtil.init(RelayrApp.get());
        try {
            mClient = new MqttClient(SslUtil.instance().getBroker(), "relayr", null);
        } catch (MqttException e) {
            e.printStackTrace();
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
            mClient.connect(SslUtil.instance().getConnectOptions(channel.getCredentials()));
            webSocketCallback.connectCallback("");
        } catch (MqttException e) {
            webSocketCallback.errorCallback(e);
            e.printStackTrace();
        }

        try {
            mClient.subscribe(channel.getCredentials().getTopic());
        } catch (MqttException e) {
            webSocketCallback.errorCallback(e);
            e.printStackTrace();
        }
    }
}
