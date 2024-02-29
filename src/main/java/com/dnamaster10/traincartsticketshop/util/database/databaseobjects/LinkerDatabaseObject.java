package com.dnamaster10.traincartsticketshop.util.database.databaseobjects;

public record LinkDatabaseObject (
        int slot,
        int linkedGuiId,
        int linkedGuiPage,
        String colouredDisplayName,
        String rawDisplayName
) {}