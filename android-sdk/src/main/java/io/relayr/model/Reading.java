package io.relayr.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A reading is the information gathered by the device.
 * Use modelId to make sense of readings data.
 */
public class Reading {

    public long received;
    public String deviceId;
    public String modelId;
    public List<Data> readings = new ArrayList<>();

    public static class Data {
        public final long recorded;
        public final String meaning;
        public final String path;
        public final Object value;

        public Data(long recorded, String meaning, String path, Object value) {
            this.recorded = recorded;
            this.meaning = meaning;
            this.path = path;
            this.value = value;
        }
    }
}
