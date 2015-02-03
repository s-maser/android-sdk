package io.relayr.model;

/** A reading is the information gathered by the device. */
public class Reading {

    public String id;
    public long received;
    public long recorded;
    public Readings readings = new Readings();

    public class Readings {
        public float humidity;
        public float temperature;
        public float noiseLevel;
        public float luminosity;
        public float proximity;
        public LightColorProx.Color color;
        public AccelGyroscope.Accelerometer acceleration;
        public AccelGyroscope.Gyroscope angularSpeed;
    }
}
