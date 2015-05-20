package io.relayr.websocket;

import android.util.Pair;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.relayr.ble.service.error.DisconnectionException;
import io.relayr.model.OnBoardingScan;
import io.relayr.model.Transmitter;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Action1;

@Singleton
public class OnBoardingClient {

    private final int SCAN_TIME = 20000;
    private final String CMD_SCAN_ON = "/cmd/scan/" + SCAN_TIME;
    private final String CMD_PRESENCE_CONNECT = "/presence/connect";
    private final String CMD_PRESENCE_DISCONNECT = "/presence/disconnect";
    private final String CMD_PRESENCE_ANNOUNCE = "/announce/#";

    private String topicPrefix;
    private final WebSocket<Transmitter> mWebSocket;
    private Observable<Boolean> mTransmitterPresence;

    @Inject
    public OnBoardingClient(WebSocketFactory factory) {
        mWebSocket = factory.createOnBoardingWebSocket();
    }

    public Observable<Transmitter> startOnBoarding(final Transmitter transmitter) {
        topicPrefix = transmitter.getTopic();

        return mWebSocket.createClient(transmitter);
    }

    public Observable<Boolean> getTransmitterPresence() {
        final String connectTopic = topicPrefix + CMD_PRESENCE_CONNECT;
        final String disconnectTopic = topicPrefix + CMD_PRESENCE_DISCONNECT;

        if (mTransmitterPresence == null)
            mTransmitterPresence = Observable
                    .create(new Observable.OnSubscribe<Boolean>() {
                        @Override
                        public void call(final Subscriber<? super Boolean> subscriber) {
                            mWebSocket.subscribe(connectTopic, null, new WebSocketCallback() {
                                @Override
                                public void connectCallback(Object message) {
                                }

                                @Override
                                public void disconnectCallback(Object message) {
                                    subscriber.onError(new DisconnectionException(""));
                                }

                                @Override
                                public void successCallback(Object message) {
                                    subscriber.onNext(true);
                                }

                                @Override
                                public void errorCallback(Throwable e) {
                                    subscriber.onError(e);
                                }
                            });
                            mWebSocket.subscribe(disconnectTopic, null, new WebSocketCallback() {
                                @Override
                                public void connectCallback(Object message) {
                                }

                                @Override
                                public void disconnectCallback(Object message) {
                                    subscriber.onError(new DisconnectionException(""));
                                }

                                @Override
                                public void successCallback(Object message) {
                                    subscriber.onNext(false);
                                }

                                @Override
                                public void errorCallback(Throwable e) {
                                    subscriber.onError(e);
                                }
                            });
                        }
                    })
                    .doOnUnsubscribe(new Action0() {
                        @Override public void call() {
                            mTransmitterPresence = null;
                            mWebSocket.unSubscribe(connectTopic);
                            mWebSocket.unSubscribe(disconnectTopic);
                        }
                    })
                    .doOnError(new Action1<Throwable>() {
                        @Override public void call(Throwable t) {
                            mTransmitterPresence = null;
                            mWebSocket.unSubscribe(connectTopic);
                            mWebSocket.unSubscribe(disconnectTopic);
                        }
                    })
                    //Cache only the last value
                    .cache(1)
                    .defaultIfEmpty(false);

        return mTransmitterPresence;
    }

    public Observable<OnBoardingScan> startScanning() {
        final String presenceTopic = topicPrefix + CMD_PRESENCE_ANNOUNCE;
        final String scanTopic = topicPrefix + CMD_SCAN_ON;

        return Observable
                .create(new Observable.OnSubscribe<OnBoardingScan>() {
                    @Override
                    public void call(final Subscriber<? super OnBoardingScan> subscriber) {
                        mWebSocket.subscribe(presenceTopic, null, new WebSocketCallback() {
                            @Override
                            public void connectCallback(Object message) {
                            }

                            @Override
                            public void disconnectCallback(Object message) {
                                subscriber.onError(new Exception("Disconnected."));
                            }

                            @Override
                            public void successCallback(Object message) {
                                Pair<String, String> scanData = (Pair<String, String>) message;
                                final String[] split = scanData.second.split("#");
                                subscriber.onNext(new OnBoardingScan(scanData.first, split[1], Integer.parseInt(split[0])));
                            }

                            @Override
                            public void errorCallback(Throwable e) {
                                subscriber.onError(e);
                            }
                        });

                        mWebSocket.publish(scanTopic, null);
                    }
                })
                .timeout(SCAN_TIME, TimeUnit.MILLISECONDS)
                .doOnUnsubscribe(new Action0() {
                    @Override public void call() {
                        mWebSocket.unSubscribe(presenceTopic);
                    }
                })
                .doOnError(new Action1<Throwable>() {
                    @Override public void call(Throwable t) {
                        mWebSocket.unSubscribe(presenceTopic);
                    }
                });
    }
}


