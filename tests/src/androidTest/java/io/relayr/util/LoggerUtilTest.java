package io.relayr.util;

import org.junit.Before;
import org.junit.BeforeClass;
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
    private LoggerUtils logUtils;

    @Before
    public void init() {
        lock = new CountDownLatch(1);

        DataStorage.saveUserToken("ut");
        MockitoAnnotations.initMocks(this);

        Observable<Boolean> mockObservable = Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                subscriber.onNext(true);
            }
        });
        when(reachUtils.isConnectedToInternet()).thenReturn(true);
        when(reachUtils.isPlatformAvailable()).thenReturn(mockObservable);
        when(reachUtils.isPlatformReachable()).thenReturn(mockObservable);

        when(cloudApi.logMessage(anyList())).thenReturn(Observable.create(new Observable
                .OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                subscriber.onNext(null);
            }
        }));

        logUtils = new LoggerUtils(cloudApi, reachUtils);
    }

    @Test
    public void logMessage_RelayrSdkTest() {
        RelayrSdk.initInMockMode(Robolectric.application.getApplicationContext());
        assertThat(RelayrSdk.logMessage("1")).isTrue();
    }

    @Test
    public void logNullMessage_RelayrSdkTest() {
        RelayrSdk.initInMockMode(Robolectric.application.getApplicationContext());
        assertThat(RelayrSdk.logMessage(null)).isTrue();
    }

    @Test
    public void logMessageFlowTest() {
        logMultiple(4);
        verifyWithDelay(0);

        logUtils.logMessage("0");
        verifyWithDelay(1);

        logUtils.logMessage("0");
        verifyWithDelay(1);

        logMultiple(4);

        verifyWithDelay(2);
    }

    @Test
    public void flushLoggedMessagesFlowTest() {
        logMultiple(4);
        verifyWithDelay(0);

        logUtils.flushLoggedMessages();
        verifyWithDelay(1);

        logUtils.logMessage("6");
        verifyWithDelay(1);
    }

    private void verifyWithDelay(int times) {
        try {
            lock.await(200, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        verify(cloudApi, times == 0 ? never() : times(times)).logMessage(anyList());
    }

    private void logMultiple(int total) {
        for (int i = 0; i < total; i++)
            logUtils.logMessage("0");
    }
}
