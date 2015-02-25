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

    class Data {
        public long recorded;
        public String meaning;
        public String path;
        public Object value;
    }
}
