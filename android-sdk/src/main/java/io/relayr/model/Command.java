package io.relayr.model;

public class Command {

    final public String path;
    final public String command;
    final public Object value;

    public Command(String path, String command, Object value) {
        this.path = path;
        this.command = command;
        this.value = value;
    }
}
