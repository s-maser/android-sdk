package io.relayr.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import io.relayr.RelayrApp;
import io.relayr.model.LogEvent;

/**
 * LogStorage wraps SharedPreferences and acts as queue.
 * Maximal number of potentially saved messages is 500.
 */
public class LogStorage {

    private static final long MAX_STORAGE = 500;
    private static final String LOG_FILE = "io.relayr.log.storage";

    static final SharedPreferences STORAGE =
            RelayrApp.get().getSharedPreferences(LOG_FILE, Context.MODE_PRIVATE);

    static Long sHead = null;
    static Long sTotal = null;

    private static long sLimit;

    static void init(int limit) {
        sLimit = limit;

        sTotal = LogPropsStorage.getTotal();
        sHead = LogPropsStorage.getHead();
    }

    static boolean isEmpty() {
        return sTotal == 0L;
    }

    /**
     * Saves message to storage and returns true when there are enough messages to remove from storage.
     *
     * @param message to log on the platform
     * @return true if number of saved messages is above the limit, false otherwise
     */
    static synchronized boolean saveMessage(LogEvent message) {
        if (message == null) return false;

        if (sTotal >= MAX_STORAGE) return true;

        STORAGE.edit().putString(moveHead().toString(), new Gson().toJson(message)).apply();

        return sTotal >= sLimit;
    }

    /**
     * Returns saved messages. Maximum of returned messages is limited on initialization.
     *
     * @return list of {@link io.relayr.model.LogEvent} objects
     */
    static synchronized List<LogEvent> loadMessages() {
        long start;

        synchronized (sHead) {
            synchronized (sTotal) {
                if (sTotal < sLimit) return new ArrayList<>();

                start = sHead - sTotal + 1;
                sTotal -= sLimit;
            }
        }

        List<LogEvent> messages = new ArrayList<>();

        for (long i = 0; i < sLimit; i++) {
            String key = "" + (start + i);
            String event = STORAGE.getString(key, null);

            if (event != null) messages.add(new Gson().fromJson(event, LogEvent.class));

            STORAGE.edit().remove(key).apply();
        }

        return messages;
    }

    /**
     * Returns all saved messages.
     *
     * @return list of {@link io.relayr.model.LogEvent} objects
     */
    static synchronized List<LogEvent> loadAllMessages() {
        synchronized (sHead) {
            synchronized (sTotal) {
                List<LogEvent> messages = new ArrayList<>();

                for (long i = sTotal; i > 0; i--) {
                    String event = STORAGE.getString("" + (sHead - i + 1), null);
                    if (event != null) messages.add(new Gson().fromJson(event, LogEvent.class));
                }

                sHead = 0L;
                sTotal = 0L;

                STORAGE.edit().clear().apply();
                LogPropsStorage.clear();

                return messages;
            }
        }
    }

    private static Long moveHead() {
        synchronized (sHead) {
            synchronized (sTotal) {
                sHead++;
                sTotal++;

                LogPropsStorage.saveHead(sHead);
                LogPropsStorage.saveTotal(sTotal);

                return sHead;
            }
        }
    }

    static class LogPropsStorage {
        private static final String PROPS_FILE = "io.relayr.log.properties.storage";
        private static final String PROPS_FILE_HEAD = "io.relayr.log.properties.storage.head";
        private static final String PROPS_FILE_TOTAL = "io.relayr.log.properties.storage.total";

        private static final SharedPreferences PROPS =
                RelayrApp.get().getSharedPreferences(PROPS_FILE, Context.MODE_PRIVATE);

        static Long getTotal() {
            Long total = PROPS.getLong(PROPS_FILE_TOTAL, -1);
            if (total == -1) total = 0L;
            return total;
        }

        static Long getHead() {
            Long head = PROPS.getLong(PROPS_FILE_HEAD, -1);
            if (head == -1) head = 0L;
            return head;
        }

        static void saveHead(Long head) {
            PROPS.edit().putLong(PROPS_FILE_HEAD, head).apply();
        }

        static void saveTotal(Long total) {
            PROPS.edit().putLong(PROPS_FILE_TOTAL, total).apply();
        }

        static void clear() {
            PROPS.edit().clear().apply();
        }
    }
}
