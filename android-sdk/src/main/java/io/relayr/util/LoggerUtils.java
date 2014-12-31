package io.relayr.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.relayr.api.CloudApi;
import io.relayr.model.LogEvent;
import io.relayr.storage.DataStorage;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

@Singleton
public class LoggerUtils {

    private static final int AUTO_FLUSH = 5;

    private static CloudApi sApi;
    private static ReachAbilityUtils sReachUtils;

    private static ConcurrentLinkedQueue<LogEvent> sEvents;

    @Inject
    LoggerUtils(CloudApi api, ReachAbilityUtils reachUtils) {
        sApi = api;
        sReachUtils = reachUtils;
        sEvents = new ConcurrentLinkedQueue<>();
    }

    public boolean logMessage(String message) {
        if (DataStorage.getUserToken().isEmpty()) return false;

        sEvents.add(new LogEvent(message == null ? "null" : message));

        if (sEvents.size() >= AUTO_FLUSH)
            sReachUtils.isPlatformReachable()
                    .subscribe(new Action1<Boolean>() {
                        @Override
                        public void call(Boolean status) {
                            if (status != null && status) logToPlatform(pollElements(AUTO_FLUSH));
                        }
                    });

        return true;
    }

    public Observable<Boolean> flushLoggedMessages() {
        if (sEvents.isEmpty()) return emptyResult();

        return sReachUtils.isPlatformReachable()
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .flatMap(new Func1<Boolean, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(Boolean status) {
                        if (!status) return emptyResult();
                        else return logToPlatform(pollElements(sEvents.size()));
                    }
                });
    }

    private Observable<Boolean> logToPlatform(final List<LogEvent> events) {
        return sApi.logMessage(events)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .flatMap(new Func1<Void, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(Void aVoid) {
                        return Observable.create(new Observable.OnSubscribe<Boolean>() {
                            @Override
                            public void call(Subscriber<? super Boolean> subscriber) {
                                subscriber.onNext(true);
                            }
                        });
                    }
                }, new Func1<Throwable, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(final Throwable throwable) {
                        return Observable.create(new Observable.OnSubscribe<Boolean>() {
                            @Override
                            public void call(Subscriber<? super Boolean> subscriber) {
                                throwable.printStackTrace();
                                subscriber.onError(throwable);
                                sEvents.addAll(events);
                            }
                        });
                    }
                }, new Func0<Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call() {
                        return Observable.create(new Observable.OnSubscribe<Boolean>() {
                            @Override
                            public void call(Subscriber<? super Boolean> subscriber) {
                                subscriber.onCompleted();
                            }
                        });
                    }
                });
    }

    private Observable<Boolean> emptyResult() {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                subscriber.onNext(false);
            }
        });
    }

    private List<LogEvent> pollElements(int total) {
        List<LogEvent> events = new ArrayList<>(total);
        for (int i = 0; i < total; i++) {
            events.add(sEvents.poll());
        }

        return events;
    }
}

