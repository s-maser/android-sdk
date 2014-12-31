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
import io.relayr.api.CloudApi;
import io.relayr.api.StatusApi;
import io.relayr.model.Status;
import io.relayr.storage.DataStorage;
import rx.Observable;
import rx.Subscriber;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class LoggerUtilTest {

    @Mock private CloudApi cloudApi;
    @Mock private StatusApi statusApi;
    @Mock private ReachAbilityUtils reachUtils;

    private CountDownLatch lock;

    @Before
    public void init() {
        DataStorage.saveUserToken("ut");

        MockitoAnnotations.initMocks(this);
        RelayrSdk.initInMockMode(Robolectric.application.getApplicationContext());
    }

    @Test
    public void logMessage_RelayrSdkTest() {
        assertThat(RelayrSdk.logMessage("1")).isTrue();
    }

    @Test
    public void logNullMessage_RelayrSdkTest() {
        assertThat(RelayrSdk.logMessage(null)).isTrue();
    }

    @Test
    public void logMessageFlowTest() {
        lock = new CountDownLatch(1);

        when(reachUtils.isPlatformReachable()).thenReturn(Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                subscriber.onNext(true);
            }
        }));

        when(cloudApi.logMessage(anyList())).thenReturn(Observable.create(new Observable
                .OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                subscriber.onNext(null);
            }
        }));

        LoggerUtils logUtils = new LoggerUtils(cloudApi, reachUtils);

        logUtils.logMessage("1");
        delayVerify(0);
        logUtils.logMessage("2");
        delayVerify(0);
        logUtils.logMessage("3");
        delayVerify(0);
        logUtils.logMessage("4");
        delayVerify(0);
        logUtils.logMessage("5");
        delayVerify(1);

        logUtils.logMessage("6");
        delayVerify(1);
    }

    public void delayVerify(int times) {
        try {
            lock.await(100, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        verify(cloudApi, times == 0 ? never() : times(times)).logMessage(anyList());
    }
}
