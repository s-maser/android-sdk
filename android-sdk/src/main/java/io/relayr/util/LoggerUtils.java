package io.relayr.util;

import java.util.List;

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

    private static boolean sLoggingData = false;

    @Inject
    LoggerUtils(CloudApi api, ReachabilityUtils reachUtils) {
        sApi = api;
        sReachUtils = reachUtils;

        LogStorage.init(AUTO_FLUSH);
    }

    public boolean logMessage(String message) {
        if (DataStorage.getUserToken().isEmpty() || message == null) return false;

        boolean ready = LogStorage.saveMessage(new LogEvent(message));

        if (ready && !sLoggingData) {
            sLoggingData = true;
            sReachUtils.isPlatformReachable()
                    .observeOn(Schedulers.newThread())
                    .subscribeOn(Schedulers.newThread())
                    .subscribe(new Action1<Boolean>() {
                        @Override
                        public void call(Boolean status) {
                            if (status != null && status) logToPlatform(LogStorage.loadMessages());
                            else sLoggingData = false;
                        }
                    });
        }

        return sReachUtils.isConnectedToInternet();
    }

    public boolean flushLoggedMessages() {
        if (LogStorage.isEmpty() || !sReachUtils.isConnectedToInternet()) return false;

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
                        if (status) logToPlatform(LogStorage.loadAllMessages());
                        else sLoggingData = false;
                    }
                });

        return true;
    }

    private void logToPlatform(final List<LogEvent> events) {
        if (events.isEmpty()) return;

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
                        sLoggingData = false;
                    }

                    @Override
                    public void onNext(Void aVoid) {
                        sLoggingData = false;
                    }
                });
    }
}

