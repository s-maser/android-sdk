package io.relayr.websocket;

import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import io.relayr.RelayrApp;
import io.relayr.model.MqttChannel;

class MqttWebSocket extends WebSocket<MqttChannel> {

    private MqttAsyncClient mClient;

    public MqttWebSocket() {
        SslUtil.init(RelayrApp.get());
        try {
            mClient = new MqttAsyncClient(SslUtil.instance().getBroker(), "relayr", null);
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
                Log.e("TAG", "deliveryComplete");
            }
        });

        try {
            final IMqttToken token = mClient.connect(SslUtil.instance().getConnectOptions(channel
                    .getCredentials()));
            token.waitForCompletion();
            webSocketCallback.connectCallback("Connected");

//            try {
//                mClient.subscribe(channel.getCredentials().getTopic());
//            } catch (MqttException e) {
//                webSocketCallback.errorCallback(e);
//                e.printStackTrace();
//            }
        } catch (MqttException e) {
            webSocketCallback.errorCallback(e);
            e.printStackTrace();
        }
    }
}
