package io.relayr.util;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import io.relayr.RelayrSdk;
import io.relayr.api.StatusApi;
import io.relayr.model.Status;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class ReachAbilityUtilTest {

    @Mock private StatusApi statusApi;

    private boolean reachable;
    private CountDownLatch lock;

    @Before
    public void init() {
        lock = new CountDownLatch(1);

        MockitoAnnotations.initMocks(this);
        RelayrSdk.initInMockMode(Robolectric.application.getApplicationContext());
    }

    @Test
    public void checkInternetConnectionTest() {
        ReachAbilityUtils utils = new ReachAbilityUtils(statusApi);
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

        ReachAbilityUtils utils = new ReachAbilityUtils(statusApi);
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
    public void checkPlatformReachAbilityWhenNotAvailableTest() {
        reachable = true;

        when(statusApi.getServerStatus()).thenReturn(Observable.create(new Observable.OnSubscribe<Status>() {
            @Override
            public void call(Subscriber<? super Status> subscriber) {
                subscriber.onError(new Throwable());
            }
        }));

        ReachAbilityUtils utils = new ReachAbilityUtils(statusApi);
        utils.isPlatformReachable()
                .subscribe(new Subscriber<Boolean>() {
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
    public void checkPlatformReachAbility_RelayrSdkTest() {
        reachable = false;

        RelayrSdk.isPlatformReachable()
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean status) {
                        reachable = status;
                    }
                });

        await();
        assertThat(reachable).isTrue();
    }

    public void await() {
        try {
            lock.await(100, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
