package io.relayr.model;

public class AccelGyroscope {
    public long ts; //":1400776389653, //Timestamp
    public Accelerometer acc;   //"accel":{"x":-0.63,"y":1.02,"z":-0.96},   //%2.2f (max range +-16.0)
    public Gyroscope gyr;        //"gyro":{"x":124.3,"y":12.2,"z":34.1}

    public static class Accelerometer {
        public float x;
        public float y;
        public float z;
    }

    public static class Gyroscope {
        public float x;
        public float y;
        public float z;
    }

}
