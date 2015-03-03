package io.relayr.model;

public class ModelCommand {

    final public String command;
    final public String path;
    final public Object unit;
    final public String minimum;
    final public String maximum;

    public ModelCommand(String command, String path, Object unit, String minimum, String maximum) {
        this.command = command;
        this.path = path;
        this.unit = unit;
        this.minimum = minimum;
        this.maximum = maximum;
    }

    @Override
    public String toString() {
        return "ModelCommand{" +
                "command='" + command + '\'' +
                ", path='" + path + '\'' +
                ", unit=" + unit +
                ", minimum='" + minimum + '\'' +
                ", maximum='" + maximum + '\'' +
                '}';
    }
}
