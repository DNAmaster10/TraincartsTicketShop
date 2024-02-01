package com.dnamaster10.tcgui.util.database.databaseobjects;

public class LinkerDatabaseObject {
    private final int slot;
    private final int linkedGuiId;
    private final int linkedGuiPage;
    private final String colouredDisplayName;
    private final String rawDisplayName;
    public int getSlot() {
        return slot;
    }
    public int getLinkedGuiId() {
        return linkedGuiId;
    }
    public int getLinkedGuiPage() {
        return linkedGuiPage;
    }
    public String getColouredDisplayName() {
        return colouredDisplayName;
    }
    public String getRawDisplayName() {
        return rawDisplayName;
    }
    public LinkerDatabaseObject(int slot, int linkedGuiId, int linkedGuiPage, String colouredDisplayName, String rawDisplayName) {
        this.slot = slot;
        this.linkedGuiId = linkedGuiId;
        this.linkedGuiPage = linkedGuiPage;
        this.colouredDisplayName = colouredDisplayName;
        this.rawDisplayName = rawDisplayName;
    }
}
