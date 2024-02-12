package com.dnamaster10.tcgui.util.database.databaseobjects;

public record LinkerDatabaseObject (
        int slot,
        int linkedGuiId,
        int linkedGuiPage,
        String colouredDisplayName,
        String rawDisplayName
) {}