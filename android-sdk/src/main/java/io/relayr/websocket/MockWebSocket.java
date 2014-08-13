package io.relayr.websocket;

import com.google.gson.Gson;

import io.relayr.api.MockBackend;
import io.relayr.model.Reading;
import io.relayr.model.WebSocketConfig;
import io.relayr.utils.DelayerUtil;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

class MockWebSocket extends WebSocket {

    private static final String TAG = MockWebSocket.class.getSimpleName();

    private final MockBackend mMockBackend;

    MockWebSocket(WebSocketConfig webSocketConfig, MockBackend mockBackend) {
        super(webSocketConfig);
        mMockBackend = mockBackend;
    }

    @Override
    public void subscribe(String channel, final WebSocketCallback webSocketCallback) {

        Observable.from(mMockBackend.getWebSocketReadings())
                .flatMap(new Func1<Reading, Observable<? extends String>>() {
                    @Override
                    public Observable<String> call(Reading reading) {
                        DelayerUtil.delay();
                        return Observable.from(new Gson().toJson(reading));
                    }
                })
                .repeat()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Object>() {
                    @Override
                    public void onCompleted() {
                        webSocketCallback.disconnectCallback("");
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        webSocketCallback.errorCallback(throwable);
                    }

                    @Override
                    public void onNext(Object o) {
                        webSocketCallback.successCallback(o);
                    }
                });

    }
}
