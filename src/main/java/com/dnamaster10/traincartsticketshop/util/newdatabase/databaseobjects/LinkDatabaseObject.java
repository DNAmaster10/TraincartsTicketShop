package com.dnamaster10.traincartsticketshop.util.newdatabase.databaseobjects;

public record LinkDatabaseObject (
        int slot,
        int linkedGuiId,
        int linkedGuiPage,
        String colouredDisplayName,
        String rawDisplayName
) {}