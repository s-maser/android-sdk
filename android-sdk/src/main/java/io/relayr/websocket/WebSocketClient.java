package io.relayr.websocket;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.relayr.SocketClient;
import io.relayr.api.ChannelApi;
import io.relayr.model.DataPackage;
import io.relayr.model.MqttChannel;
import io.relayr.model.MqttDefinition;
import io.relayr.model.Reading;
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
    final Map<String, PublishSubject<Reading>> mSocketConnections = new HashMap<>();

    @Inject
    public WebSocketClient(ChannelApi channelApi, WebSocketFactory factory) {
        mChannelApi = channelApi;
        mWebSocket = factory.createWebSocket();
    }

    @Override
    public Observable<Reading> subscribe(TransmitterDevice device) {
        if (mSocketConnections.containsKey(device.id))
            return mSocketConnections.get(device.id);
        else
            return start(device);
    }

    private synchronized Observable<Reading> start(final TransmitterDevice device) {
        final PublishSubject<Reading> subject = PublishSubject.create();
        mSocketConnections.put(device.id, subject);

        mChannelApi.create(new MqttDefinition(device.id, "mqtt"))
                .flatMap(new Func1<MqttChannel, Observable<MqttChannel>>() {
                    @Override
                    public Observable<MqttChannel> call(final MqttChannel channel) {
                        return mWebSocket.createClient(channel);
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
                                    final PublishSubject<Reading> subject) {
        mWebSocket.subscribe(channel.getCredentials().getTopic(), channel.getChannelId(), new WebSocketCallback() {
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
            public void successCallback(Object message) {
                DataPackage dataPackage = new Gson().fromJson(message.toString(), DataPackage.class);
                for (DataPackage.Data dataPoint : dataPackage.readings) {
                    subject.onNext(new Reading(dataPackage.received, dataPoint.recorded,
                            dataPoint.meaning, dataPoint.path, dataPoint.value));
                }
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
            if (mWebSocket.unSubscribe(mDeviceChannels.get(deviceId).getCredentials().getTopic()))
                mDeviceChannels.remove(deviceId);
    }
}

