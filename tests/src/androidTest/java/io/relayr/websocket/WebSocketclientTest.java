package io.relayr.websocket;

import com.google.gson.reflect.TypeToken;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.robolectric.Robolectric;

import io.relayr.TestEnvironment;
import io.relayr.api.ChannelApi;
import io.relayr.api.MockBackend;
import io.relayr.model.MqttChannel;
import io.relayr.model.MqttDefinition;
import io.relayr.model.TransmitterDevice;
import rx.Observable;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class WebSocketClientTest extends TestEnvironment {

    @Mock private ChannelApi channelApi;
    @Mock private WebSocketFactory webSocketFactory;
    @Mock private WebSocket<MqttChannel> webSocket;

    private WebSocketClient socketClient;

    @Before
    public void init() {
        super.init();

        initSdk();
        inject();
    }

    @Test
    public void webSocketClientSubscribeTest() {
        Observable<MqttChannel> observable = new MockBackend(Robolectric.application)
                .createObservable(new TypeToken<MqttChannel>() {
                }, MockBackend.MQTT_CREDENTIALS);

        when(webSocketFactory.createWebSocket()).thenReturn(webSocket);
        when(channelApi.create(any(MqttDefinition.class))).thenReturn(observable);

        socketClient = new WebSocketClient(channelApi, webSocketFactory);
        socketClient.subscribe(createTransmitterDevice());
        await();

        verify(webSocketFactory, times(1)).createWebSocket();
        await();

        verify(channelApi, times(1)).create(any(MqttDefinition.class));
        await();

        verify(webSocket, times(1)).subscribe(any(MqttChannel.class),
                any(WebSocketCallback.class));
    }

    @Test
    public void webSocketClientUnSubscribeTest() {
        when(webSocketFactory.createWebSocket()).thenReturn(webSocket);
        when(webSocket.unSubscribe(any(MqttChannel.class))).thenReturn(true);

        socketClient = new WebSocketClient(channelApi, webSocketFactory);
        socketClient.subscribe(createTransmitterDevice());
        await();

        assertThat(socketClient.mSocketConnections.isEmpty()).isFalse();

        socketClient.unSubscribe("id");
        await();

        assertThat(socketClient.mSocketConnections.isEmpty()).isTrue();
    }

    private TransmitterDevice createTransmitterDevice() {
        return new TransmitterDevice("id", "s", "o", "n", "m");
    }
}
