package com.dnamaster10.traincartsticketshop.util.database.databaseobjects;

public class PlayerDatabaseObject {
    //TODO convert to record
    private final String USERNAME;
    private final String UUID;
    public String getUsername() {
        return this.USERNAME;
    }
    public String getUuid() {
        return this.UUID;
    }
    public PlayerDatabaseObject(String username, String uuid) {
        this.USERNAME = username;
        this.UUID = uuid;
    }
}
