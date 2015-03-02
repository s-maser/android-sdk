package io.relayr.websocket;

import com.google.gson.Gson;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.relayr.api.MockBackend;
import io.relayr.model.MqttChannel;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

class MockWebSocket extends WebSocket<MqttChannel> {

    private final MockBackend mMockBackend;

    MockWebSocket(MockBackend mockBackend) {
        mMockBackend = mockBackend;
    }

    @Override
    public boolean subscribe(MqttChannel channel, final WebSocketCallback callback) {
        callback.connectCallback("");
        Observable.from(mMockBackend.getWebSocketReadings())
                .delay(1, TimeUnit.SECONDS)
                .map(new Func1<Object, String>() {
                    @Override
                    public String call(Object reading) {
                        return new Gson().toJson(reading);
                    }
                })
                .repeat()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Object>() {
                    @Override
                    public void onCompleted() {
                        callback.disconnectCallback("");
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        callback.errorCallback(throwable);
                    }

                    @Override
                    public void onNext(Object o) {
                        callback.successCallback(o);
                    }
                });
        return true;
    }

    @Override
    void createClient(MqttChannel channel, Subscriber<Void> subscriber) {
        subscriber.onNext(null);
    }

    @Override
    boolean unSubscribe(MqttChannel topic) {
        return true;
    }
}
