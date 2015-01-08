package io.relayr.util;

import com.google.gson.Gson;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.fest.assertions.api.Assertions.assertThat;

import io.relayr.RelayrSdk;
import io.relayr.model.LogEvent;

@RunWith(RobolectricTestRunner.class)
public class LogStorageTest {

    private CountDownLatch lock;

    @Before
    public void before() {
        RelayrSdk.initInMockMode(Robolectric.application.getApplicationContext());
        LogStorage.init(3);

        lock = new CountDownLatch(1);
    }

    @After
    public void after() {
        LogStorage.STORAGE.edit().clear().apply();
        LogStorage.LogPropsStorage.clear();
    }

    @Test
    public void saveMultipleMsgsTest() {
        LogEvent l = new LogEvent("");

        assertThat(LogStorage.saveMessage(l)).isFalse();
        assertThat(LogStorage.saveMessage(l)).isFalse();
        assertThat(LogStorage.saveMessage(l)).isTrue();

        assertThat(LogStorage.STORAGE.contains("1")).isTrue();
        assertThat(LogStorage.STORAGE.contains("2")).isTrue();
        assertThat(LogStorage.STORAGE.contains("3")).isTrue();

        assertThat(LogStorage.sHead).isEqualTo(3);
        assertThat(LogStorage.sTotal).isEqualTo(3);
    }

    @Test
    public void loadMessagesTest() {
        LogEvent l1 = new LogEvent("1");
        LogEvent l2 = new LogEvent("2");
        LogEvent l3 = new LogEvent("3");
        LogEvent l4 = new LogEvent("4");

        assertThat(LogStorage.saveMessage(l1)).isFalse();
        assertThat(LogStorage.saveMessage(l2)).isFalse();
        assertThat(LogStorage.saveMessage(l3)).isTrue();
        assertThat(LogStorage.saveMessage(l4)).isTrue();

        assertThat(LogStorage.sHead).isEqualTo(4);
        assertThat(LogStorage.sTotal).isEqualTo(4);

        List<LogEvent> events = LogStorage.loadMessages();
        assertThat(events.isEmpty()).isFalse();
        assertThat(events.size()).isEqualTo(3);

        assertThat(events.containsAll(Arrays.asList(l1, l2, l3)));

        assertThat(LogStorage.sHead).isEqualTo(4);
        assertThat(LogStorage.sTotal).isEqualTo(1);

        assertThat(LogStorage.STORAGE.contains("1")).isFalse();
        assertThat(LogStorage.STORAGE.contains("4")).isTrue();
    }

    @Test
    public void loadAllMessagesTest() {
        LogEvent l1 = new LogEvent("1");

        assertThat(LogStorage.saveMessage(l1)).isFalse();
        assertThat(LogStorage.saveMessage(l1)).isFalse();
        assertThat(LogStorage.saveMessage(l1)).isTrue();
        assertThat(LogStorage.saveMessage(l1)).isTrue();
        assertThat(LogStorage.saveMessage(l1)).isTrue();

        List<LogEvent> events = LogStorage.loadAllMessages();
        assertThat(events.isEmpty()).isFalse();
        assertThat(events.size()).isEqualTo(5);

        assertThat(events.containsAll(Arrays.asList(l1, l1, l1, l1, l1)));

        assertThat(LogStorage.sHead).isEqualTo(0);
        assertThat(LogStorage.sTotal).isEqualTo(0);

        assertThat(LogStorage.STORAGE.contains(new Gson().toJson(l1))).isFalse();
    }

    @Test
    public void saveMultipleMsgsSynchronizationTest() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            for (int i = 0; i < 10; i++) {
                                LogStorage.saveMessage(new LogEvent("" + i));
                            }
                        }
                    }).start();
                }
            }
        }).start();

        await();

        assertThat(LogStorage.sHead).isEqualTo(100);
        assertThat(LogStorage.sTotal).isEqualTo(100);

        assertThat(LogStorage.STORAGE.getString("100", null)).isNotNull();
        assertThat(LogStorage.STORAGE.getString("101", null)).isNull();
    }

    @Test
    public void saveAndLoadMultipleMsgsSynchronizationTest() {
        for (int i = 0; i < 5; i++)
            LogStorage.saveMessage(new LogEvent(""));

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 100; i++) {
                    if (i % 5 == 0) LogStorage.saveMessage(new LogEvent("" + i));
                    if (i % 50 == 0)
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                LogStorage.loadMessages();
                            }
                        }).start();
                }
            }
        }).start();

        await();

        assertThat(LogStorage.sHead).isEqualTo(25);
        assertThat(LogStorage.sTotal).isEqualTo(19);

        assertThat(LogStorage.STORAGE.getString("7", null)).isNotNull();
        assertThat(LogStorage.STORAGE.getString("6", null)).isNull();
    }

    @Test
    public void loadAllMsgsSynchronizationTest() {
        for (int i = 0; i < 100; i++)
            LogStorage.saveMessage(new LogEvent(""));

        List<LogEvent> logEvents = LogStorage.loadAllMessages();
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10; i++)
                    LogStorage.saveMessage(new LogEvent(""));
            }
        }).start();

        await();

        assertThat(logEvents.size()).isEqualTo(100);

        assertThat(LogStorage.sHead).isEqualTo(10);
        assertThat(LogStorage.sTotal).isEqualTo(10);

        assertThat(LogStorage.STORAGE.getString("50", null)).isNull();
    }

    private void await() {
        try {
            lock.await(200, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
