package io.relayr.model;

public class ModelReading {

    final public String meaning;
    final public Object unit;
    final public Object minimum;
    final public Object maximum;
    final public Object precision;

    public ModelReading(String meaning, Object unit, Object minimum, Object maximum, Object precision) {
        this.meaning = meaning;
        this.unit = unit;
        this.minimum = minimum;
        this.maximum = maximum;
        this.precision = precision;
    }

    @Override
    public String toString() {
        return "ModelReading{" +
                "meaning='" + meaning + '\'' +
                ", unit='" + unit + '\'' +
                ", minimum='" + minimum + '\'' +
                ", maximum='" + maximum + '\'' +
                ", precision='" + precision + '\'' +
                '}';
    }
}
