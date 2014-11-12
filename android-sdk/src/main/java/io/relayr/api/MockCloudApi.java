package io.relayr.api;

import android.util.Log;

import com.google.gson.Gson;

import java.util.List;

import io.relayr.model.LogEvent;
import rx.Observable;
import rx.Subscriber;

public class MockCloudApi implements CloudApi {

    private static final String TAG = "MockCloudApi";

    @Override
    public Observable<Void> logMessage(final List<LogEvent> events) {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                for (LogEvent event: events) {
                    Log.d(TAG, new Gson().toJson(event));
                }
                subscriber.onNext(null);
            }
        });
    }
}
