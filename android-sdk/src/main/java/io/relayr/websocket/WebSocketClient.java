package io.relayr.websocket;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.relayr.RelayrSdk;
import io.relayr.SocketClient;
import io.relayr.api.SubscriptionApi;
import io.relayr.model.App;
import io.relayr.model.MqttChannel;
import io.relayr.model.MqttDefinition;
import io.relayr.model.TransmitterDevice;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

@Singleton
public class WebSocketClient implements SocketClient {

    private final WebSocket mWebSocket;
    private final SubscriptionApi mSubscriptionApi;
    private final Map<String, PublishSubject<Object>> mWebSocketConnections = new HashMap<>();

    @Inject
    public WebSocketClient(SubscriptionApi subscriptionApi, WebSocketFactory factory) {
        mSubscriptionApi = subscriptionApi;
        mWebSocket = factory.createWebSocket();
    }

    public Observable<Object> subscribe(TransmitterDevice device) {
        if (mWebSocketConnections.containsKey(device.id)) {
            return mWebSocketConnections.get(device.id);
        } else {
            return start(device);
        }
    }

    private Observable<Object> start(final TransmitterDevice device) {
        final PublishSubject<Object> subject = PublishSubject.create();
        mWebSocketConnections.put(device.id, subject);

        RelayrSdk.getRelayrApi().getAppInfo()
                .flatMap(new Func1<App, Observable<MqttChannel>>() {
                    @Override
                    public Observable<MqttChannel> call(App app) {
                        return mSubscriptionApi.subscribeToMqtt(new MqttDefinition(device.id, "mqtt"));
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread())
                .subscribe(new Subscriber<MqttChannel>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mWebSocketConnections.remove(device.id);
                    }

                    @Override
                    public void onNext(MqttChannel credentials) {
                        Log.e("WebSocketClient", credentials.toString());
                        mWebSocket.createClient(credentials.getCredentials().getClientId());
                        subscribeToChannel(credentials, device.id, subject);
                    }
                });

        return subject.observeOn(AndroidSchedulers.mainThread())
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        unSubscribe(device.id);
                    }
                });
    }

    private void subscribeToChannel(MqttChannel credentials,
                                    final String deviceId,
                                    final PublishSubject<Object> subject) {

        mWebSocket.subscribe(credentials, new WebSocketCallback() {
            @Override
            public void connectCallback(Object message) {
                Log.i("WebSocketClient", "Connected: " + message.toString());
            }

            @Override
            public void disconnectCallback(Object message) {
                subject.onCompleted();
                mWebSocketConnections.remove(deviceId);
            }

            @Override
            public void reconnectCallback(Object message) {
            }

            @Override
            public void successCallback(Object message) {
                subject.onNext(message);
            }

            @Override
            public void errorCallback(Throwable e) {
                subject.onError(e);
                mWebSocketConnections.clear();
            }
        });
    }

    @Override
    public void unSubscribe(final String deviceId) {
        if (mWebSocketConnections.containsKey(deviceId)) {
            mWebSocketConnections.get(deviceId).onCompleted();
            mWebSocketConnections.remove(deviceId);
        }

//        mWebSocket.unSubscribe(deviceId);

        RelayrSdk.getRelayrApi()
                .getAppInfo()
                .flatMap(new Func1<App, Observable<Void>>() {
                    @Override
                    public Observable<Void> call(App app) {
                        return mSubscriptionApi.unSubscribe(app.id, deviceId);
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread())
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void conf) {
                        /* success! */
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable e) {
                    }
                });
    }
}

