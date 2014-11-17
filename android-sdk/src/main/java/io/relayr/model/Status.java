package io.relayr.model;

import java.io.Serializable;

public class Status implements Serializable{

    private String database;

    public Status(String database) {
        this.database = database;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Status)) return false;

        Status status = (Status) o;

        if (database != null ? !database.equals(status.database) : status.database != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return database != null ? database.hashCode() : 0;
    }
}
