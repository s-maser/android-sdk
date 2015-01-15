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
    private final int AUTO_FLUSH = 5;

    private  CloudApi mApi;
    private  ReachabilityUtils mReachUtils;

    //Used for synchronizing flushing and auto sending logged messages
    private volatile boolean loggingData = false;

    @Inject
    LoggerUtils(CloudApi api, ReachabilityUtils reachUtils) {
        mApi = api;
        mReachUtils = reachUtils;

        LogStorage.init(AUTO_FLUSH);

        if (LogStorage.oldMessagesExist()) flushLoggedMessages();
    }

    public boolean logMessage(String message) {
        if (DataStorage.getUserToken().isEmpty() || message == null) return false;

        boolean ready = LogStorage.saveMessage(new LogEvent(message));

        if (ready && !loggingData) {
            loggingData = true;
            mReachUtils.isPlatformReachable()
                    .observeOn(Schedulers.newThread())
                    .subscribe(new Action1<Boolean>() {
                        @Override
                        public void call(Boolean status) {
                            if (status != null && status) logToPlatform(LogStorage.loadMessages());
                            else loggingData = false;
                        }
                    });
        }

        return mReachUtils.isConnectedToInternet();
    }

    public boolean flushLoggedMessages() {
        loggingData = true;

        if (LogStorage.isEmpty() || !mReachUtils.isConnectedToInternet()) {
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
                        if (status) logToPlatform(LogStorage.loadAllMessages());
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

