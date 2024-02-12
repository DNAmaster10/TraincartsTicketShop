package com.dnamaster10.tcgui.util.database.databaseobjects;

public record TicketDatabaseObject (
        int slot,
        String tcName,
        String colouredDisplayName,
        String rawDisplayName
) {}
