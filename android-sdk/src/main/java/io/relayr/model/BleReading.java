package io.relayr.model;

/** A reading is the information gathered by the device. */
public class BleReading {

    public String id;
    public long received;
    public long recorded;
    public Readings readings = new Readings();

    public class Readings {
        public int humidity;
        public float temperature;
        public int noiseLevel;
        public int luminosity;
        public int proximity;
        public LightColorProx.Color color;
        public AccelGyroscope.Accelerometer acceleration;
        public AccelGyroscope.Gyroscope angularSpeed;
    }
}
