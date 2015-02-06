package io.relayr.websocket;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.relayr.RelayrSdk;
import io.relayr.SocketClient;
import io.relayr.api.ChannelApi;
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

    final ChannelApi mChannelApi;
    final WebSocket<MqttChannel> mWebSocket;
    final Map<String, MqttChannel> mDeviceChannels = new HashMap<>();
    final Map<String, PublishSubject<Object>> mSocketConnections = new HashMap<>();

    @Inject
    public WebSocketClient(ChannelApi channelApi, WebSocketFactory factory) {
        mChannelApi = channelApi;
        mWebSocket = factory.createWebSocket();
    }

    public Observable<Object> subscribe(TransmitterDevice device) {
        if (mSocketConnections.containsKey(device.id))
            return mSocketConnections.get(device.id);
        else
            return start(device);
    }

    private synchronized Observable<Object> start(final TransmitterDevice device) {
        final PublishSubject<Object> subject = PublishSubject.create();
        mSocketConnections.put(device.id, subject);

        RelayrSdk.getRelayrApi().getAppInfo()
                .flatMap(new Func1<App, Observable<MqttChannel>>() {
                    @Override
                    public Observable<MqttChannel> call(App app) {
                        return mChannelApi.create(new MqttDefinition(device.id, "mqtt"));
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Subscriber<MqttChannel>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mSocketConnections.remove(device.id);
                    }

                    @Override
                    public void onNext(MqttChannel channel) {
                        mWebSocket.createClient(channel.getCredentials().getClientId());
                        subscribeToChannel(channel, device.id, subject);
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

    private void subscribeToChannel(final MqttChannel channel, final String deviceId,
                                    final PublishSubject<Object> subject) {
        mWebSocket.subscribe(channel, new WebSocketCallback() {
            @Override
            public void connectCallback(Object message) {
                if (!mDeviceChannels.containsKey(deviceId))
                    mDeviceChannels.put(deviceId, channel);
            }

            @Override
            public void disconnectCallback(Object message) {
                subject.onError((Throwable) message);
                mDeviceChannels.remove(deviceId);
                mSocketConnections.remove(deviceId);
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
                mDeviceChannels.remove(deviceId);
                mSocketConnections.remove(deviceId);
            }
        });
    }

    @Override
    public void unSubscribe(final String deviceId) {
        if (mSocketConnections.containsKey(deviceId)) {
            mSocketConnections.get(deviceId).onCompleted();
            mSocketConnections.remove(deviceId);
        }

        if (!mDeviceChannels.isEmpty() && mDeviceChannels.containsKey(deviceId))
            if (mWebSocket.unSubscribe(mDeviceChannels.get(deviceId)))
                mDeviceChannels.remove(deviceId);
    }
}

