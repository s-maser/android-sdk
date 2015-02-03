package io.relayr.api;

import com.google.gson.reflect.TypeToken;

import javax.inject.Inject;

import io.relayr.model.MqttChannel;
import io.relayr.model.MqttDefinition;
import io.relayr.model.WebSocketConfig;
import rx.Observable;
import rx.Subscriber;

import static io.relayr.api.MockBackend.APPS_DEVICES_START;
import static io.relayr.api.MockBackend.MQTT_CREDENTIALS;

public class MockSubscriptionApi implements SubscriptionApi {

    private final MockBackend mMockBackend;

    @Inject
    public MockSubscriptionApi(MockBackend mockBackend) {
        mMockBackend = mockBackend;
    }

    @Override
    public Observable<WebSocketConfig> subscribe(String appId, String deviceId) {
        return mMockBackend.createObservable(new TypeToken<WebSocketConfig>() { },
                APPS_DEVICES_START);
    }

    @Override
    public Observable<Void> unSubscribe(String appId, String deviceId) {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                subscriber.onNext(null);
                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<MqttChannel> subscribeToMqtt(MqttDefinition mqttDefinition) {
        return mMockBackend.createObservable(new TypeToken<MqttChannel>() { },
                MQTT_CREDENTIALS);
    }

}
