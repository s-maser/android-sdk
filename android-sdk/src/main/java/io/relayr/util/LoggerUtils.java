package io.relayr.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.relayr.api.CloudApi;
import io.relayr.model.LogEvent;
import io.relayr.storage.DataStorage;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

@Singleton
public class LoggerUtils {

    private static final int AUTO_FLUSH = 5;

    private static CloudApi sApi;
    private static ReachAbilityUtils sReachUtils;

    private static ConcurrentLinkedQueue<LogEvent> sEvents;
    private static boolean loggingData = false;

    @Inject
    LoggerUtils(CloudApi api, ReachAbilityUtils reachUtils) {
        sApi = api;

        sReachUtils = reachUtils;
        sEvents = new ConcurrentLinkedQueue<>();
    }

    public boolean logMessage(String message) {
        if (DataStorage.getUserToken().isEmpty()) return false;

        sEvents.add(new LogEvent(message == null ? "null" : message));

        if (sEvents.size() >= AUTO_FLUSH && !loggingData) {
            loggingData = true;
            sReachUtils.isPlatformReachable()
                    .observeOn(Schedulers.io())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Action1<Boolean>() {
                        @Override
                        public void call(Boolean status) {
                            if (status != null && status) logToPlatform(pollElements(AUTO_FLUSH));
                        }
                    });
        }

        return sReachUtils.isConnectedToInternet();
    }

    public boolean flushLoggedMessages() {
        if (sEvents.isEmpty() || !sReachUtils.isConnectedToInternet()) return false;

        loggingData = true;

        sReachUtils.isPlatformAvailable()
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean status) {
                        if (status) logToPlatform(pollElements(sEvents.size()));
                    }
                });

        return true;
    }

    private void logToPlatform(final List<LogEvent> events) {
        sApi.logMessage(events)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Void>() {
                    @Override
                    public void onCompleted() {
                        loggingData = false;
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        sEvents.addAll(events);
                        loggingData = false;
                    }

                    @Override
                    public void onNext(Void aVoid) {
                        loggingData = false;
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

