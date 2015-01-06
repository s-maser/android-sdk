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
import rx.functions.Action1;
import rx.schedulers.Schedulers;

@Singleton
public class LoggerUtils {

    private static final int AUTO_FLUSH = 5;

    private static CloudApi sApi;
    private static ReachabilityUtils sReachUtils;

    private static ConcurrentLinkedQueue<LogEvent> sEvents;
    private static boolean sLoggingData = false;

    @Inject
    LoggerUtils(CloudApi api, ReachabilityUtils reachUtils) {
        sApi = api;

        sReachUtils = reachUtils;
        sEvents = new ConcurrentLinkedQueue<>();
    }

    public boolean logMessage(String message) {
        if (DataStorage.getUserToken().isEmpty()) return false;

        sEvents.add(new LogEvent(message == null ? "null" : message));

        if (sEvents.size() >= AUTO_FLUSH && !sLoggingData) {
            sLoggingData = true;
            sReachUtils.isPlatformReachable()
                    .observeOn(Schedulers.newThread())
                    .subscribeOn(Schedulers.newThread())
                    .subscribe(new Action1<Boolean>() {
                        @Override
                        public void call(Boolean status) {
                            if (status != null && status) logToPlatform(pollElements(AUTO_FLUSH));
                            else sLoggingData = false;
                        }
                    });
        }

        return sReachUtils.isConnectedToInternet();
    }

    public boolean flushLoggedMessages() {
        if (sEvents.isEmpty() || !sReachUtils.isConnectedToInternet()) return false;

        final int eventsToFlush = sEvents.size();

        sLoggingData = true;

        sReachUtils.isPlatformAvailable()
                .observeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {
                        sLoggingData = false;
                    }

                    @Override
                    public void onError(Throwable e) {
                        sLoggingData = false;
                    }

                    @Override
                    public void onNext(Boolean status) {
                        if (status) logToPlatform(pollElements(eventsToFlush));
                        else sLoggingData = false;
                    }
                });

        return true;
    }

    private void logToPlatform(final List<LogEvent> events) {
        if(events.isEmpty()) return;

        sApi.logMessage(events)
                .observeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Subscriber<Void>() {
                    @Override
                    public void onCompleted() {
                        sLoggingData = false;
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        sEvents.addAll(events);
                        sLoggingData = false;
                    }

                    @Override
                    public void onNext(Void aVoid) {
                        sLoggingData = false;
                    }
                });
    }

    private List<LogEvent> pollElements(int total) {
        synchronized (new Object()) {
            int elements = sEvents.size() < total ? sEvents.size() : total;

            List<LogEvent> events = new ArrayList<>(elements);
            for (int i = 0; i < elements; i++) {
                events.add(sEvents.poll());
            }

            return events;
        }
    }
}

