package com.dnamaster10.tcgui.objects;

public class Ticket {
    public static enum Type {
        WARP,
        TRAINCART
    }
    Type type;
    String name;
    String displayName;
    public Ticket(Type type, String name, String displayName) {
        this.type = type;
        this.name = name;
        this.displayName = displayName;
    }
}
