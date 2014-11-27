package io.relayr.model;

import java.io.Serializable;

public class ReadingMeaning implements Serializable {

    private final String key;
    private final String value;

    public ReadingMeaning(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "ReadingMeaning{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
