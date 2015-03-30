package io.relayr.websocket;

import android.util.Pair;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.relayr.model.Transmitter;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

@Singleton
public class OnBoardingClient {

    private final int SCAN_TIME = 5000;
    private final String CMD_SCAN_ON = "/scan/";
    private final String CMD_CONNECT_DEVICE = "/connect/";
    private final String CMD_PRESENCE_CONNECT = "/presence/connect";
    private final String CMD_PRESENCE_DISCONNECT = "/presence/disconnect";
    private final String CMD_PRESENCE_ANNOUNCE = "/announce/#";

    private final WebSocket<Transmitter> mWebSocket;
    private String topicPrefix;
    private String commandPrefix;

    @Inject
    public OnBoardingClient(WebSocketFactory factory) {
        mWebSocket = factory.createOnBoardingWebSocket();
    }

    public Observable<Object> startOnBoarding(final Transmitter transmitter) {
        topicPrefix = transmitter.getTopic();
        commandPrefix = topicPrefix + "/cmd/";

        return mWebSocket.createClient(transmitter)
                .flatMap(new Func1<Transmitter, Observable<Object>>() {
                    @Override
                    public Observable<Object> call(Transmitter transmitter) {
                        return subscribeToTransmitterMessages();
                    }
                });
    }

    private Observable<Object> subscribeToTransmitterMessages() {
        return Observable.create(new Observable.OnSubscribe<Object>() {
            @Override
            public void call(final Subscriber<? super Object> subscriber) {
                mWebSocket.subscribe(topicPrefix + CMD_PRESENCE_CONNECT, null, new WebSocketCallback() {
                    @Override
                    public void connectCallback(Object message) {
                    }

                    @Override
                    public void disconnectCallback(Object message) {
                        subscriber.onError(new Exception("Transmitter disconnected."));
                    }

                    @Override
                    public void successCallback(Object message) {
                        subscriber.onNext(message);
                    }

                    @Override
                    public void errorCallback(Throwable e) {
                        subscriber.onError(e);
                    }
                });
                mWebSocket.subscribe(topicPrefix + CMD_PRESENCE_DISCONNECT, null, new WebSocketCallback() {
                    @Override
                    public void connectCallback(Object message) {
                    }

                    @Override
                    public void disconnectCallback(Object message) {
                        subscriber.onError(new Exception("Transmitter disconnected."));
                    }

                    @Override
                    public void successCallback(Object message) {
                        subscriber.onError(new Exception("Transmitter disconnected."));
                    }

                    @Override
                    public void errorCallback(Throwable e) {
                        subscriber.onError(e);
                    }
                });
            }
        });
    }

    public Observable<Pair<String, String>> startScanning() {
        return Observable.create(new Observable.OnSubscribe<Pair<String, String>>() {
            @Override
            public void call(final Subscriber<? super Pair<String, String>> subscriber) {
                mWebSocket.subscribe(topicPrefix + CMD_PRESENCE_ANNOUNCE, null, new WebSocketCallback() {
                    @Override
                    public void connectCallback(Object message) {
                    }

                    @Override
                    public void disconnectCallback(Object message) {
                        subscriber.onError(new Exception("Disconnected."));
                    }

                    @Override
                    public void successCallback(Object message) {
                        subscriber.onNext((Pair<String, String>) message);
                    }

                    @Override
                    public void errorCallback(Throwable e) {
                        subscriber.onError(e);
                    }
                });
                mWebSocket.publish(commandPrefix + CMD_SCAN_ON + SCAN_TIME, null);

                ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
                service.schedule(new Runnable() {
                    @Override
                    public void run() {
                        subscriber.onCompleted();
                    }
                }, SCAN_TIME, TimeUnit.MILLISECONDS);
            }
        });
    }

    public void connectToDevice(String macAddress) {
        String cleanedAddress = macAddress.replace(":", "");
        mWebSocket.publish(commandPrefix + CMD_CONNECT_DEVICE + cleanedAddress, null);
    }
}


