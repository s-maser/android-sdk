package io.relayr.websocket;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import io.relayr.TestEnvironment;
import io.relayr.model.IntegrationType;
import io.relayr.model.Transmitter;
import rx.Observable;
import rx.Subscriber;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class OnBoardingClientTest extends TestEnvironment {

    @Mock private WebSocketFactory webSocketFactory;
    @Mock private WebSocket<Transmitter> webSocket;

    @Before
    public void init() {
        super.init();

        initSdk();
        inject();
    }

    @Test
    public void webSocketClientSubscribeTest() {
        when(webSocket.createClient(any(Transmitter.class))).thenReturn(Observable.create(new Observable.OnSubscribe<Transmitter>() {
            @Override
            public void call(Subscriber<? super Transmitter> subscriber) {
                subscriber.onNext(createTransmitterDevice());
            }
        }));
        when(webSocket.subscribe(anyString(), anyString(), any(WebSocketCallback.class))).thenReturn(true);

        when(webSocketFactory.createOnBoardingWebSocket()).thenReturn(webSocket);

        OnBoardingClient mSocketClient = new OnBoardingClient(webSocketFactory);
        mSocketClient.startOnBoarding(createTransmitterDevice()).subscribe();
        await();

        verify(webSocket, times(1)).createClient(any(Transmitter.class));

        await();
        verify(webSocket, times(1)).subscribe(eq("topic/presence/connect"), anyString(), any(WebSocketCallback
                .class));
    }

    private Transmitter createTransmitterDevice() {
        final Transmitter transmitter = new Transmitter("o", "name", IntegrationType.WUNDERBAR_2);
        transmitter.setTopic("topic");
        return transmitter;
    }
}
