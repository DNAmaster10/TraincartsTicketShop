package com.dnamaster10.traincartsticketshop.util.newdatabase.databaseobjects;

public record TicketDatabaseObject (
        int slot,
        String tcName,
        String colouredDisplayName,
        String rawDisplayName,
        String purchaseMessage
) {}
