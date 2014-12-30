package io.relayr.websocket;

import com.google.gson.reflect.TypeToken;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.robolectric.Robolectric;

import io.relayr.TestEnvironment;
import io.relayr.api.MockBackend;
import io.relayr.api.SubscriptionApi;
import io.relayr.model.MqttChannel;
import io.relayr.model.TransmitterDevice;
import rx.Observable;
import rx.observers.TestSubscriber;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class WebSocketClientTest extends TestEnvironment {

    @Mock private SubscriptionApi subscriptionApi;
    @Mock private WebSocketFactory webSocketFactory;
    @Mock private WebSocket<MqttChannel> webSocket;

    private WebSocketClient socketClient;

    @Before
    public void init() {
        super.init();

        initSdk();
    }

    @Test
    public void webSocketClientSubscribeTest() {
        Observable<MqttChannel> observable = new MockBackend(Robolectric.application)
                .createObservable(new TypeToken<MqttChannel>() {
                }, MockBackend.MQTT_CREDENTIALS);

        when(webSocketFactory.createWebSocket()).thenReturn(webSocket);
        when(subscriptionApi.subscribeToMqtt(anyString(), anyString())).thenReturn(observable);

        socketClient = new WebSocketClient(subscriptionApi, webSocketFactory);
        socketClient.subscribe(createTransmitterDevice(), new TestSubscriber<Object>() {
        });

        await();

        verify(webSocketFactory, times(1)).createWebSocket();
        verify(subscriptionApi, times(1)).subscribeToMqtt(USER_ID, APP_NAME);
        verify(webSocket, times(1)).subscribe(any(MqttChannel.class),
                any(WebSocketCallback.class));
    }
    
    @Test
    public void webSocketClientUnSubscribeTest() {
        when(subscriptionApi.unSubscribe(anyString(), anyString())).thenReturn(Observable.<Void>empty());

        socketClient = new WebSocketClient(subscriptionApi, webSocketFactory);
        socketClient.unSubscribe(APP_NAME);

        await();

        verify(subscriptionApi, times(1)).unSubscribe(USER_ID, APP_NAME);
    }

    @Test
    public void webSocketClientFlowTest() {
        socketClient = new WebSocketClient(subscriptionApi, webSocketFactory);

        verify(subscriptionApi, never()).subscribeToMqtt(USER_ID, APP_NAME);

        socketClient.subscribe(createTransmitterDevice(), new TestSubscriber<Object>() {
        });

        await();
        verify(subscriptionApi, times(1)).subscribeToMqtt(USER_ID, APP_NAME);

        socketClient.subscribe(createTransmitterDevice(), new TestSubscriber<Object>() {
        });

        await();
        verify(subscriptionApi, times(1)).subscribeToMqtt(USER_ID, APP_NAME);

        socketClient.unSubscribe(APP_NAME);

        await();
        verify(subscriptionApi, times(1)).unSubscribe(USER_ID, APP_NAME);

        socketClient.subscribe(createTransmitterDevice(), new TestSubscriber<Object>() {
        });

        await();
        verify(subscriptionApi, times(2)).subscribeToMqtt(USER_ID, APP_NAME);
    }

    private TransmitterDevice createTransmitterDevice() {
        return new TransmitterDevice(APP_NAME, "s", "o", "n", "m");
    }
}
