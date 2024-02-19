package com.dnamaster10.traincartsticketshop.util.database.databaseobjects;

public record LinkerDatabaseObject (
        int slot,
        int linkedGuiId,
        int linkedGuiPage,
        String colouredDisplayName,
        String rawDisplayName
) {}