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
import io.relayr.model.TransmitterDevice;
import io.relayr.model.WebSocketConfig;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

@Singleton
public class WebSocketClient implements SocketClient {

    private static final String TAG = WebSocketClient.class.getSimpleName();

    private final WebSocket mWebSocket;
    private final SubscriptionApi mSubscriptionApi;
    private final Map<String, PublishSubject<Object>> mWebSocketConnections = new HashMap<>();

    @Inject
    public WebSocketClient(SubscriptionApi subscriptionApi, WebSocketFactory factory) {
        mSubscriptionApi = subscriptionApi;

        mWebSocket = factory.createWebSocket();
    }

    @Override
    public Subscription subscribe(TransmitterDevice device, Subscriber<Object> subscriber) {
        String deviceId = device.id;

        if (!mWebSocketConnections.containsKey(deviceId)) return start(deviceId, subscriber);
        else return subscribe(mWebSocketConnections.get(deviceId), subscriber);
    }

    private Subscription subscribe(PublishSubject<Object> subject, Subscriber<Object> subscriber) {
        return subject.observeOn(AndroidSchedulers.mainThread()).subscribe(subscriber);
    }

    private Subscription start(final String deviceId, final Subscriber<Object> subscriber) {
        final PublishSubject<Object> subject = PublishSubject.create();

        RelayrSdk.getRelayrApi()
                .getAppInfo()
                .flatMap(new Func1<App, Observable<MqttChannel>>() {
                    @Override
                    public Observable<MqttChannel> call(App app) {
                        return mSubscriptionApi.subscribeToMqtt(app.id, deviceId);
                    }
                })
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<MqttChannel>() {

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, e.getMessage());
                        e.printStackTrace();
                        mWebSocketConnections.remove(deviceId);
                    }

                    @Override
                    public void onNext(MqttChannel credentials) {
                        subscribeToChannel(credentials, deviceId, subject);
                    }
                });

        mWebSocketConnections.put(deviceId, subject);

        return subscribe(subject, subscriber);
    }

    private void subscribeToChannel(MqttChannel credentials,
                                    final String deviceId,
                                    final PublishSubject<Object> subject) {

        mWebSocket.subscribe(credentials, new WebSocketCallback() {
            @Override
            public void connectCallback(Object message) {
                System.err.println("connectCallback");
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
            public void errorCallback(Throwable error) {
                subject.onError(error);
                mWebSocketConnections.clear();
            }
        });
    }

    @Override
    public void unSubscribe(final String sensorId) {
        if (mWebSocketConnections.containsKey(sensorId)) {
            mWebSocketConnections.get(sensorId).onCompleted();
            mWebSocketConnections.remove(sensorId);
        }

        // mWebSocket.unSubscribe(sensorId);

        RelayrSdk.getRelayrApi()
                .getAppInfo()
                .flatMap(new Func1<App, Observable<Void>>() {
                    @Override
                    public Observable<Void> call(App app) {
                        return mSubscriptionApi.unSubscribe(app.id, sensorId);
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void pubnubConfig) {
                        /* success! */
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable error) {
                    }
                });
    }
}
