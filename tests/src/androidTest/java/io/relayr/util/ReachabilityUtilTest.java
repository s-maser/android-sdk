package io.relayr.util;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import io.relayr.api.StatusApi;
import io.relayr.model.Status;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class ReachabilityUtilTest {

    @Mock private StatusApi statusApi;

    private boolean reachable;
    private CountDownLatch lock;
    private ReachabilityUtils utils;

    @Before
    public void init() {
        lock = new CountDownLatch(1);

        MockitoAnnotations.initMocks(this);

        utils = new ReachabilityUtils(statusApi);
    }

    @Test
    public void checkInternetConnectionTest() {
        assertThat(utils.isConnectedToInternet()).isTrue();
    }

    @Test
    public void checkPlatformAvailabilityTest() {
        reachable = false;

        when(statusApi.getServerStatus()).thenReturn(Observable.create(new Observable.OnSubscribe<Status>() {
            @Override
            public void call(Subscriber<? super Status> subscriber) {
                subscriber.onNext(new Status("ok"));
            }
        }));

        utils.isPlatformAvailable().subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean status) {
                reachable = status;
            }
        });

        await();
        assertThat(reachable).isTrue();
    }

    @Test
    public void checkPlatformReachAbilityTest() {
        reachable = false;

        when(statusApi.getServerStatus()).thenReturn(Observable.create(new Observable.OnSubscribe<Status>() {
            @Override
            public void call(Subscriber<? super Status> subscriber) {
                subscriber.onNext(new Status("ok"));
            }
        }));

        utils.isPlatformReachable().subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean status) {
                reachable = status;
            }
        });

        await();
        assertThat(reachable).isTrue();
    }

    @Test
    public void checkWhenPlatformNotAvailableTest() {
        reachable = true;

        when(statusApi.getServerStatus()).thenReturn(Observable.create(new Observable.OnSubscribe<Status>() {
            @Override
            public void call(Subscriber<? super Status> subscriber) {
                subscriber.onError(new Throwable());
            }
        }));

        utils.isPlatformReachable().subscribe(new Subscriber<Boolean>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                reachable = false;
            }

            @Override
            public void onNext(Boolean status) {
            }
        });

        await();
        assertThat(reachable).isFalse();
    }

    @Test
    public void checkPermissionTest() {
        final String PERMISSION_INTERNET = "android.permission.INTERNET";

        assertThat(utils.isPermissionGranted(PERMISSION_INTERNET)).isTrue();
    }

    @Test
    public void checkFaultyPermissionTest() {
        final String PERMISSION_INTERNET = "";

        assertThat(utils.isPermissionGranted(PERMISSION_INTERNET)).isFalse();
    }

    @Test
    public void checkUnExistingPermissionTest() {
        final String PERMISSION_INTERNET = "android.permission.ACCESS_WIFI_STATE";

        assertThat(utils.isPermissionGranted(PERMISSION_INTERNET)).isFalse();
    }

    public void await() {
        try {
            lock.await(100, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
