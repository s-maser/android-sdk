package io.relayr.websocket;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import io.relayr.model.WebSocketConfig;

public class MqttWebSocket {

    //    {
//        "channelId": "0bfc0cd2-3952-4a61-9511-59b360a19ccf",
//            "credentials": {
//        "user": "b9ecd88a-81dc-4505-b5c4-02a2e0f3e136:4b4ee05e-b57d-44be-a7d3-046827ebda72",
//                "password": "aXUSt1syTW5l",
//                "topic": "/v1/0bfc0cd2-3952-4a61-9511-59b360a19ccf"
//              }
//    }

    private IMqttClient mClient;

    String username = "b9ecd88a-81dc-4505-b5c4-02a2e0f3e136:4b4ee05e-b57d-44be-a7d3-046827ebda72";
    String password = "aXUSt1syTW5l";
    String topic = "/v1/0bfc0cd2-3952-4a61-9511-59b360a19ccf";
    String tcpBroker = "tcp://mqtt.relayr.io:1883";
    String sslBroker = "tcp://mqtt.relayr.io:8883";
    String clientId = "JavaSample";

    MemoryPersistence persistence = new MemoryPersistence();

    MqttWebSocket(WebSocketConfig webSocketConfig) {
        try {
            mClient = new MqttClient(sslBroker, clientId, persistence);
        } catch (MqttException e) {
            e.printStackTrace();
        }

        System.setProperty("javax.net.ssl.keyStore", "file:///android_asset/relayr.crt");
        System.setProperty("javax.net.ssl.keyStorePassword", "relayr");
        System.setProperty("javax.net.ssl.trustStore", "file:///android_asset/relayr.crt");

        try {
            mClient.connect(getConnectOptions());
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    void subscribe(String channel, final WebSocketCallback webSocketCallback) {
        try {
            mClient.subscribe(topic);
        } catch (MqttException e) {
            e.printStackTrace();
        }
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
                try {
                    webSocketCallback.connectCallback(token.getMessage());
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private MqttConnectOptions getConnectOptions() {
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(true);
        connOpts.setConnectionTimeout(3);
        connOpts.setKeepAliveInterval(10);
        connOpts.setUserName(username);
        connOpts.setPassword(password.toCharArray());

        return connOpts;
    }
}
