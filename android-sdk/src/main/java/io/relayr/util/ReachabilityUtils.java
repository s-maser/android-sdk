package io.relayr.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.relayr.RelayrApp;
import io.relayr.RelayrSdk;
import io.relayr.api.StatusApi;
import io.relayr.model.Status;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

@Singleton
public class ReachabilityUtils {

    private static final String TAG = "io.relayr.util.ReachabilityUtils";

    private StatusApi mApi;
    private Map<String, Boolean> sPermissions;

    @Inject
    ReachabilityUtils(StatusApi api) {
        mApi = api;
        sPermissions = new HashMap<>();
    }

    public Observable<Boolean> isPlatformReachable() {
        if (!isConnectedToInternet()) return emptyResult();
        else return isPlatformAvailable();
    }

    public boolean isConnectedToInternet() {
        if (!isPermissionGranted(RelayrSdk.PERMISSION_NETWORK)) return false;

        ConnectivityManager manager = (ConnectivityManager) RelayrApp.get().getSystemService(Context
                .CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnected();
    }

    public boolean isPermissionGranted(String permission) {
        if (permission == null || permission.isEmpty()) return false;

        if (sPermissions.get(permission) != null) return sPermissions.get(permission);

        Context appContext = RelayrApp.get().getApplicationContext();
        try {
            PackageInfo info = appContext.getPackageManager()
                    .getPackageInfo(appContext.getPackageName(), PackageManager.GET_PERMISSIONS);
            if (info.requestedPermissions != null) {
                for (String p : info.requestedPermissions) {
                    if (p.equals(permission)) {
                        sPermissions.put(permission, true);
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.e(TAG, "To be able to use Reacability utils please add " + permission + " permission " +
                "to AndroidManifest file.");

        sPermissions.put(permission, false);

        return false;
    }

    public Observable<Boolean> isPlatformAvailable() {
        if (!isPermissionGranted(RelayrSdk.PERMISSION_INTERNET)) return emptyResult();

        return mApi.getServerStatus()
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
