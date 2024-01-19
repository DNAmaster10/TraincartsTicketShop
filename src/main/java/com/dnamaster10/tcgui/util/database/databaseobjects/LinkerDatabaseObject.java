package com.dnamaster10.tcgui.util.database.databaseobjects;

public class LinkerDatabaseObject {
    private final int slot;
    private final int linkedGuiId;
    private final String displayName;
    public int getSlot() {
        return slot;
    }
    public int getLinkedGuiId() {
        return linkedGuiId;
    }
    public String getDisplayName() {
        return displayName;
    }
    public LinkerDatabaseObject(int slot, int linkedGuiId, String displayName) {
        this.slot = slot;
        this.linkedGuiId = linkedGuiId;
        this.displayName = displayName;
    }
}
