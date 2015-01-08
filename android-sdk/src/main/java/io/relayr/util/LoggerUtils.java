package io.relayr.util;

import android.util.Log;

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

    private final String TAG = LoggerUtils.class.getSimpleName();

    private static final int AUTO_FLUSH = 5;

    private static CloudApi sApi;
    private static ReachabilityUtils sReachUtils;

    //Used for synchronizing flushing and auto sending logged messages
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
        sLoggingData = true;

        if (LogStorage.isEmpty() || !sReachUtils.isConnectedToInternet()) {
            sLoggingData = false;
            return false;
        }

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
                        if (e.getMessage() != null) Log.w(TAG, e.getMessage());
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

    private void logToPlatform(List<LogEvent> events) {
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
                        if (e.getMessage() != null) Log.w(TAG, e.getMessage());
                        sLoggingData = false;
                    }

                    @Override
                    public void onNext(Void aVoid) {
                        sLoggingData = false;
                    }
                });
    }
}

