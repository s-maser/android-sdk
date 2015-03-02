package io.relayr.api;

import com.google.gson.reflect.TypeToken;

import javax.inject.Inject;

import io.relayr.model.MqttChannel;
import io.relayr.model.MqttDefinition;
import io.relayr.model.MqttExistingChannel;
import retrofit.http.Body;
import retrofit.http.Path;
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
    public Observable<MqttChannel> create(@Body MqttDefinition mqttDefinition) {
        return mMockBackend.createObservable(new TypeToken<MqttChannel>() {
        }, MQTT_CREDENTIALS);
    }

    @Override
    public Observable<Void> delete(@Path("channelId") String channelId) {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                subscriber.onNext(null);
                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<MqttExistingChannel> getChannels(@Path("deviceId") String deviceId) {
        return Observable.empty();
    }
}
