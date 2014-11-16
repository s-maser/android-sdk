package io.relayr.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class LogEvent {

    private static final DateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");

    static {
        sDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    private final String message;
    private final String timestamp;

    public LogEvent(String message) {
        this.message = message;
        this.timestamp = sDateFormat.format(new Date());
    }

}
