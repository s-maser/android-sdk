package io.relayr.model;

import java.io.Serializable;

public class OnBoardingScan implements Serializable {

    public int rssi;
    public String mac;
    public String model;

    public OnBoardingScan(String model, String mac, int rssi) {
        this.model = model;
        this.mac = mac;
        this.rssi = rssi;
    }

    @Override public String toString() {
        return "OnBoardingScan{" +
                "rssi=" + rssi +
                ", mac='" + mac + '\'' +
                ", model='" + model + '\'' +
                '}';
    }
}
