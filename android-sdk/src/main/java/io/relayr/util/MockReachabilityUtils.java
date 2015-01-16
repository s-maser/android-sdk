package io.relayr.util;

import javax.inject.Inject;

import io.relayr.api.StatusApi;
import rx.Observable;
import rx.Subscriber;

public class MockReachabilityUtils extends ReachabilityUtils {

    @Inject
    MockReachabilityUtils(StatusApi api) {
        super(api);
    }

    @Override
    public Observable<Boolean> isPlatformReachable() {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> sub) {
                sub.onNext(true);
            }
        });
    }

    @Override

    public boolean isConnectedToInternet() {
        return true;
    }

    @Override
    public boolean isPermissionGranted(String permission) {
        return true;
    }
}
