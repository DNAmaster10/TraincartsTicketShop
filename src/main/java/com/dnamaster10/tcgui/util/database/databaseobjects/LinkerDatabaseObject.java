package com.dnamaster10.tcgui.util.database.databaseobjects;

public class LinkerDatabaseObject {
    private final int slot;
    private final int linkedGuiId;
    private final String colouredDisplayName;
    private final String rawDisplayName;
    public int getSlot() {
        return slot;
    }
    public int getLinkedGuiId() {
        return linkedGuiId;
    }
    public String getColouredDisplayName() {
        return colouredDisplayName;
    }
    public String getRawDisplayName() {
        return rawDisplayName;
    }
    public LinkerDatabaseObject(int slot, int linkedGuiId, String colouredDisplayName, String rawDisplayName) {
        this.slot = slot;
        this.linkedGuiId = linkedGuiId;
        this.colouredDisplayName = colouredDisplayName;
        this.rawDisplayName = rawDisplayName;
    }
}
