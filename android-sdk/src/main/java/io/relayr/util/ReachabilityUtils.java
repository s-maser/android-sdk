package io.relayr.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.relayr.RelayrApp;
import io.relayr.api.StatusApi;
import io.relayr.model.Status;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

@Singleton
public class ReachabilityUtils {

    private static StatusApi sApi;

    @Inject
    ReachabilityUtils(StatusApi api) {
        sApi = api;
    }

    public Observable<Boolean> isPlatformReachable() {
        if (!isConnectedToInternet()) return emptyResult();
        else return isPlatformAvailable();
    }

    boolean isConnectedToInternet() {
        ConnectivityManager manager = (ConnectivityManager) RelayrApp.get().getSystemService(Context
                .CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnected();
    }

    Observable<Boolean> isPlatformAvailable() {
        return sApi.getServerStatus()
                .subscribeOn(Schedulers.newThread())
                .map(new Func1<Status, Boolean>() {
                    @Override
                    public Boolean call(Status status) {
                        return status != null && status.getDatabase().equals("ok");
                    }
                });
    }

    private Observable<Boolean> emptyResult() {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> sub) {
                sub.onNext(false);
            }
        });
    }
}
