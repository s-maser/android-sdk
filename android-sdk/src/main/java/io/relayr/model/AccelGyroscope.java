package io.relayr.model;

public class AccelGyroscope {
    public long ts; //":1400776389653, //Timestamp
    public Acceleration acceleration;   //"accel":{"x":-0.63,"y":1.02,"z":-0.96},   //%2.2f (max range +-16.0)
    public AngularSpeed angularSpeed;        //"gyro":{"x":124.3,"y":12.2,"z":34.1}

    public static class Acceleration {
        public float x;
        public float y;
        public float z;
    }

    public static class AngularSpeed {
        public float x;
        public float y;
        public float z;
    }

}
