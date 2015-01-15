package io.relayr.log;

import android.util.Log;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.relayr.api.CloudApi;
import io.relayr.model.LogEvent;
import io.relayr.storage.DataStorage;
import io.relayr.util.ReachabilityUtils;
import rx.Subscriber;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

@Singleton
public class Logger {

    private static final String TAG = "io.relayr.log.LoggerUtils";
    private static final int AUTO_FLUSH = 5;

    private final CloudApi mApi;
    private final ReachabilityUtils mReachUtils;

    //Used for synchronizing flushing and auto sending logged messages
    private volatile boolean loggingData = false;

    @Inject
    Logger(CloudApi api, ReachabilityUtils reachUtils) {
        mApi = api;
        mReachUtils = reachUtils;

        LoggerStorage.init(AUTO_FLUSH);

        if (LoggerStorage.oldMessagesExist()) flushLoggedMessages();
    }

    public boolean logMessage(String message) {
        if (DataStorage.getUserToken().isEmpty() || message == null) return false;

        boolean ready = LoggerStorage.saveMessage(new LogEvent(message));

        if (ready && !loggingData) {
            loggingData = true;
            mReachUtils.isPlatformReachable()
                    .observeOn(Schedulers.newThread())
                    .subscribe(new Action1<Boolean>() {
                        @Override
                        public void call(Boolean status) {
                            if (status != null && status) logToPlatform(LoggerStorage.loadMessages());
                            else loggingData = false;
                        }
                    });
        }

        return mReachUtils.isConnectedToInternet();
    }

    public boolean flushLoggedMessages() {
        loggingData = true;

        if (LoggerStorage.isEmpty() || !mReachUtils.isConnectedToInternet()) {
            loggingData = false;
            return false;
        }

        mReachUtils.isPlatformAvailable()
                .observeOn(Schedulers.newThread())
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {
                        loggingData = false;
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e.getMessage() != null) Log.w(TAG, e.getMessage());
                        loggingData = false;
                    }

                    @Override
                    public void onNext(Boolean status) {
                        if (status) logToPlatform(LoggerStorage.loadAllMessages());
                        else loggingData = false;
                    }
                });

        return true;
    }

    private void logToPlatform(List<LogEvent> events) {
        if (events.isEmpty()) return;

        mApi.logMessage(events)
                .observeOn(Schedulers.newThread())
                .subscribe(new Subscriber<Void>() {
                    @Override
                    public void onCompleted() {
                        loggingData = false;
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e.getMessage() != null) Log.w(TAG, e.getMessage());
                        loggingData = false;
                    }

                    @Override
                    public void onNext(Void aVoid) {
                        loggingData = false;
                    }
                });
    }
}

