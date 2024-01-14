package com.dnamaster10.tcgui.util;

public class TicketDatabaseObject {
    private int slot;
    private String tcName;
    private String displayName;
    private int price;
    public int getSlot() {
        return slot;
    }
    public String getTcName() {
        return tcName;
    }
    public String getDisplayName() {
        return displayName;
    }
    public int getPrice() {
        return price;
    }
    public TicketDatabaseObject (int slot, String tcName, String displayName, int price) {
        this.slot = slot;
        this.tcName = tcName;
        this.displayName = displayName;
        this.price = price;
    }
}
