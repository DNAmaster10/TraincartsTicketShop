package com.dnamaster10.tcgui.util.database.databaseobjects;

public class TicketDatabaseObject {
    private final int slot;
    private final String tcName;
    private final String colouredDisplayName;
    private final String rawDisplayName;
    private final int price;
    public int getSlot() {
        return slot;
    }
    public String getTcName() {
        return tcName;
    }
    public String getColouredDisplayName() {
        return colouredDisplayName;
    }
    public String getRawDisplayName() {
        return rawDisplayName;
    }
    public int getPrice() {
        return price;
    }
    public TicketDatabaseObject (int slot, String tcName, String colouredDisplayName, String rawDisplayName, int price) {
        this.slot = slot;
        this.tcName = tcName;
        this.colouredDisplayName = colouredDisplayName;
        this.rawDisplayName = rawDisplayName;
        this.price = price;
    }
}
