package io.relayr.log;

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

import io.relayr.RelayrSdk;
import io.relayr.model.LogEvent;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
public class LoggerStorageTest {

    private CountDownLatch lock;

    @Before
    public void before() {
        new RelayrSdk.Builder(Robolectric.application).inMockMode(true).build();
        LoggerStorage.init(3);

        lock = new CountDownLatch(1);
    }

    @After
    public void after() {
        LoggerStorage.STORAGE.edit().clear().apply();
        LoggerStorage.LoggerPropertiesStorage.clear();
    }

    @Test
    public void saveMultipleMsgsTest() {
        LogEvent l = new LogEvent("");

        assertThat(LoggerStorage.saveMessage(l)).isFalse();
        assertThat(LoggerStorage.saveMessage(l)).isFalse();
        assertThat(LoggerStorage.saveMessage(l)).isTrue();

        assertThat(LoggerStorage.STORAGE.contains("1")).isTrue();
        assertThat(LoggerStorage.STORAGE.contains("2")).isTrue();
        assertThat(LoggerStorage.STORAGE.contains("3")).isTrue();

        assertThat(LoggerStorage.sHead).isEqualTo(3);
        assertThat(LoggerStorage.sTotal).isEqualTo(3);
    }

    @Test
    public void loadMessagesTest() {
        LogEvent l1 = new LogEvent("1");
        LogEvent l2 = new LogEvent("2");
        LogEvent l3 = new LogEvent("3");
        LogEvent l4 = new LogEvent("4");

        assertThat(LoggerStorage.saveMessage(l1)).isFalse();
        assertThat(LoggerStorage.saveMessage(l2)).isFalse();
        assertThat(LoggerStorage.saveMessage(l3)).isTrue();
        assertThat(LoggerStorage.saveMessage(l4)).isTrue();

        assertThat(LoggerStorage.sHead).isEqualTo(4);
        assertThat(LoggerStorage.sTotal).isEqualTo(4);

        List<LogEvent> events = LoggerStorage.loadMessages();
        assertThat(events.isEmpty()).isFalse();
        assertThat(events.size()).isEqualTo(3);

        assertThat(events.containsAll(Arrays.asList(l1, l2, l3)));

        assertThat(LoggerStorage.sHead).isEqualTo(4);
        assertThat(LoggerStorage.sTotal).isEqualTo(1);

        assertThat(LoggerStorage.STORAGE.contains("1")).isFalse();
        assertThat(LoggerStorage.STORAGE.contains("4")).isTrue();
    }

    @Test
    public void loadAllMessagesTest() {
        LogEvent l1 = new LogEvent("1");

        assertThat(LoggerStorage.saveMessage(l1)).isFalse();
        assertThat(LoggerStorage.saveMessage(l1)).isFalse();
        assertThat(LoggerStorage.saveMessage(l1)).isTrue();
        assertThat(LoggerStorage.saveMessage(l1)).isTrue();
        assertThat(LoggerStorage.saveMessage(l1)).isTrue();

        List<LogEvent> events = LoggerStorage.loadAllMessages();
        assertThat(events.isEmpty()).isFalse();
        assertThat(events.size()).isEqualTo(5);

        assertThat(events.containsAll(Arrays.asList(l1, l1, l1, l1, l1)));

        assertThat(LoggerStorage.sHead).isEqualTo(0);
        assertThat(LoggerStorage.sTotal).isEqualTo(0);

        assertThat(LoggerStorage.STORAGE.contains(new Gson().toJson(l1))).isFalse();
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
                                LoggerStorage.saveMessage(new LogEvent("" + i));
                            }
                        }
                    }).start();
                }
            }
        }).start();

        await();

        assertThat(LoggerStorage.sHead).isEqualTo(100);
        assertThat(LoggerStorage.sTotal).isEqualTo(100);

        assertThat(LoggerStorage.STORAGE.getString("100", null)).isNotNull();
        assertThat(LoggerStorage.STORAGE.getString("101", null)).isNull();
    }

    @Test
    public void saveAndLoadMultipleMsgsSynchronizationTest() {
        for (int i = 0; i < 5; i++)
            LoggerStorage.saveMessage(new LogEvent(""));

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 100; i++) {
                    if (i % 5 == 0) LoggerStorage.saveMessage(new LogEvent("" + i));
                    if (i % 50 == 0)
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                LoggerStorage.loadMessages();
                            }
                        }).start();
                }
            }
        }).start();

        await();

        assertThat(LoggerStorage.sHead).isEqualTo(25);
        assertThat(LoggerStorage.sTotal).isEqualTo(19);

        assertThat(LoggerStorage.STORAGE.getString("7", null)).isNotNull();
        assertThat(LoggerStorage.STORAGE.getString("6", null)).isNull();
    }

    @Test
    public void loadAllMsgsSynchronizationTest() {
        for (int i = 0; i < 100; i++)
            LoggerStorage.saveMessage(new LogEvent(""));

        List<LogEvent> logEvents = LoggerStorage.loadAllMessages();
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10; i++)
                    LoggerStorage.saveMessage(new LogEvent(""));
            }
        }).start();

        await();

        assertThat(logEvents.size()).isEqualTo(100);

        assertThat(LoggerStorage.sHead).isEqualTo(10);
        assertThat(LoggerStorage.sTotal).isEqualTo(10);

        assertThat(LoggerStorage.STORAGE.getString("50", null)).isNull();
    }

    @Test
    public void loadOldMessages_shouldFlush() {
        LoggerStorage.saveMessage(new LogEvent("1"));

        assertThat(LoggerStorage.isEmpty()).isFalse();

        LoggerStorage.init(3);
        LoggerStorage.init(3);

        assertThat(LoggerStorage.isEmpty()).isFalse();

        LoggerStorage.init(3);
        assertThat(LoggerStorage.oldMessagesExist()).isTrue();
    }

    @Test
    public void loadOldMessages_shouldNOTFlush() {
        LoggerStorage.saveMessage(new LogEvent("1"));

        assertThat(LoggerStorage.isEmpty()).isFalse();

        LoggerStorage.init(3);
        LoggerStorage.init(3);

        LoggerStorage.saveMessage(new LogEvent("2"));

        assertThat(LoggerStorage.isEmpty()).isFalse();

        LoggerStorage.init(3);

        assertThat(LoggerStorage.oldMessagesExist()).isFalse();
    }

    private void await() {
        try {
            lock.await(200, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
