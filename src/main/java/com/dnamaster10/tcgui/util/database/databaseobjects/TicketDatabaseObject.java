package com.dnamaster10.tcgui.util.database.databaseobjects;

public class TicketDatabaseObject {
    private final int slot;
    private final String tcName;
    private final String displayName;
    private final int price;
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
