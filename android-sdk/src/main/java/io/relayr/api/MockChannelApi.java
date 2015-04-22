package io.relayr.api;

import com.google.gson.reflect.TypeToken;

import javax.inject.Inject;

import io.relayr.model.MqttChannel;
import io.relayr.model.MqttDefinition;
import io.relayr.model.MqttExistingChannel;
import rx.Observable;
import rx.Subscriber;

import static io.relayr.api.MockBackend.MQTT_CREDENTIALS;

public class MockChannelApi implements ChannelApi {

    private final MockBackend mMockBackend;

    @Inject
    public MockChannelApi(MockBackend mockBackend) {
        mMockBackend = mockBackend;
    }

    @Override
    public Observable<MqttChannel> create( MqttDefinition mqttDefinition) {
        return mMockBackend.createObservable(new TypeToken<MqttChannel>() {
        }, MQTT_CREDENTIALS);
    }

    @Override
    public Observable<Void> delete(String channelId) {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                subscriber.onNext(null);
                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<MqttExistingChannel> getChannels(String deviceId) {
        return Observable.empty();
    }
}
