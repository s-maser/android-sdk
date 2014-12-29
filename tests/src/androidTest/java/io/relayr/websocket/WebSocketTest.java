package io.relayr.websocket;

import org.fest.assertions.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;

import io.relayr.TestEnvironment;
import io.relayr.api.SubscriptionApi;
import io.relayr.model.MqttChannel;
import rx.Observer;

public class WebSocketTest extends TestEnvironment {

    @Inject SubscriptionApi subscriptionApi;

    private boolean success;
    private WebSocket<MqttChannel> socket;
    private MqttChannel createdChannel;

    @Before
    public void init() {
        super.init();
        inject();

        success = false;

        socket = new MqttWebSocket();

        if (createdChannel == null) {
            subscriptionApi.subscribeToMqtt(USER_ID, "dev_id")
                    .subscribe(new Observer<MqttChannel>() {
                        @Override
                        public void onCompleted() {
                            countDown();
                        }

                        @Override
                        public void onError(Throwable e) {
                            countDown();
                        }

                        @Override
                        public void onNext(MqttChannel mqttChannel) {
                            createdChannel = mqttChannel;
                            countDown();
                        }
                    });

            await();
        }
    }

    @Test
    public void webSocketConnectionTest() {
        socket.subscribe(createdChannel, new WebSocketCallback() {
            @Override
            public void connectCallback(Object o) {
                success = true;
                countDown();
            }

            @Override
            public void disconnectCallback(Object o) {
            }

            @Override
            public void reconnectCallback(Object o) {
            }

            @Override
            public void successCallback(Object o) {
            }

            @Override
            public void errorCallback(Throwable throwable) {
            }
        });

        await();

        Assertions.assertThat(success).isTrue();
    }

    @Test
    public void webSocketConnectionFailTest() {
        socket.subscribe(getChannel(), new WebSocketCallback() {
            @Override
            public void connectCallback(Object o) {
                success = true;
                countDown();
            }

            @Override
            public void disconnectCallback(Object o) {
            }

            @Override
            public void reconnectCallback(Object o) {
            }

            @Override
            public void successCallback(Object o) {
            }

            @Override
            public void errorCallback(Throwable throwable) {
            }
        });

        await();

        Assertions.assertThat(success).isFalse();
    }

    @Test
    public void webSocketSubscriptionTest() {
        socket.subscribe(createdChannel, new WebSocketCallback() {
            @Override
            public void connectCallback(Object o) {
            }

            @Override
            public void disconnectCallback(Object o) {
                success = true;
            }

            @Override
            public void reconnectCallback(Object o) {
                success = true;
            }

            @Override
            public void successCallback(Object o) {
                success = true;
            }

            @Override
            public void errorCallback(Throwable throwable) {
                success = true;
            }
        });

        await();

        Assertions.assertThat(success).isFalse();
    }

    private MqttChannel getChannel() {
        String password = "pass";
        String topic = "/v1/channelID";
        String channelId = "channelID";

        MqttChannel.MqttCredentials cred = new MqttChannel.MqttCredentials(null, password, topic);
        return new MqttChannel(channelId, cred);
    }
}
