package io.relayr.model;

public class Command {

    final public String path;
    final public String meaning;
    final public Object value;

    public Command(String path, String meaning, Object value) {
        this.path = path;
        this.meaning = meaning;
        this.value = value;
    }
}
