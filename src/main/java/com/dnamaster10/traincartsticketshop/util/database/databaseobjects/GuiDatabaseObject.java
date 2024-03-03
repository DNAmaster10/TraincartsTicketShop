package com.dnamaster10.traincartsticketshop.util.database.databaseobjects;

public record GuiDatabaseObject (
        int id,
        String name,
        String displayName,
        String ownerUuid
) {}
