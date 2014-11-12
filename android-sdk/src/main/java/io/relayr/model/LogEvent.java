package io.relayr.model;

public class LogEvent {

    private final String message;
    private final long timestamp;

    public LogEvent(String message) {
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }

}
