package io.relayr.model;

/** A reading is the information gathered by the device. */
public class MqttReading {

    public String id;
    public long received;
    public long recorded;
    public Readings readings;

    public class Readings {
        public float humidity;
        public float temperature;
        public float noiseLevel;
        public float luminosity;
        public float proximity;
        public Color color;
        public AccelGyroscope.Accelerometer angularSpeed;
        public AccelGyroscope.Gyroscope acceleration;
    }

    public class Color {
        public int red;
        public int green;
        public int blue;

        /**
         * Return a color-int from red, green, blue components.
         * The alpha component is implicitly 255 (fully opaque).
         * These component values should be [0..255], but there is no
         * range check performed, so if they are out of range, the
         * returned color is undefined.
         * <p/>
         * <pre> {@code
         * <p/>
         * int rgb = color.toRgb();
         * Bitmap image = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888);
         * image.eraseColor(c);
         * <p/>
         * } </pre>
         */
        public int toRgb() {
            float rr = red;
            float gg = green;
            float bb = blue;

            //relative correction
            rr *= 2.0 / 3.0;

            //normalize
            float max = Math.max(rr, Math.max(gg, bb));
            if (max == 0) max = 1;
            rr = (rr / max) * 255;
            gg = (gg / max) * 255;
            bb = (bb / max) * 255;

            return android.graphics.Color.rgb((int) rr, (int) gg, (int) bb);
        }
    }

    public class AccelGyroscope {

        public class Accelerometer {
            public float x;
            public float y;
            public float z;

            @Override
            public String toString() {
                return "Accelerometer{" +
                        "x=" + x +
                        ", y=" + y +
                        ", z=" + z +
                        '}';
            }
        }

        public class Gyroscope {
            public float x;
            public float y;
            public float z;

            @Override
            public String toString() {
                return "Gyroscope{" +
                        "x=" + x +
                        ", y=" + y +
                        ", z=" + z +
                        '}';
            }
        }
    }
}
