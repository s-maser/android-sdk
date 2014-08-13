package io.relayr.model;

public class LightColorProx {
    public long ts;     //"ts":1400776389653,
    public long light;  //"light":65535,                           //format: 16 bit unsigned, range: 0-65535
    public Color clr;   //"clr":{"r":65535,"g":65535,"b":65535}, //format: 16 bit unsigned, range: 0-65535
    public long prox;   //"prox":65535

    public static class Color {
        public int r;
        public int g;
        public int b;
    }

}
