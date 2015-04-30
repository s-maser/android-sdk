package io.relayr.websocket;

import io.relayr.model.Transmitter;
import rx.Observable;
import rx.Subscriber;

class MockOnBoardWebSocket extends WebSocket<Transmitter> {

    @Override
    public Observable<Transmitter> createClient(final Transmitter channel) {
        return Observable.create(new Observable.OnSubscribe<Transmitter>() {
            @Override
            public void call(Subscriber<? super Transmitter> subscriber) {
                subscriber.onNext(channel);
            }
        });
    }

    @Override
    public boolean unSubscribe(String topic) {
        return true;
    }

    @Override
    public boolean subscribe(String topic, String channelId, final WebSocketCallback callback) {
        callback.connectCallback("");
        callback.successCallback(null);
        return true;
    }
}
