package com.dnamaster10.traincartsticketshop.util.database.caches;

import com.dnamaster10.traincartsticketshop.util.database.AccessorFactory;
import com.dnamaster10.traincartsticketshop.util.database.accessorinterfaces.PlayerAccessor;
import com.dnamaster10.traincartsticketshop.util.database.databaseobjects.PlayerDatabaseObject;
import com.dnamaster10.traincartsticketshop.util.exceptions.QueryException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlayerCache {
    private static final List<String> usernames = new ArrayList<>();
    private static final HashMap<String, String> usernameUuidMap = new HashMap<>();
    private static final HashMap<String, String> uuidUsernameMap = new HashMap<>();

    public static void initialize() throws QueryException {
        PlayerAccessor playerAccessor = AccessorFactory.getPlayerAccessor();

        List<PlayerDatabaseObject> players = playerAccessor.getAllPlayers();
        for (PlayerDatabaseObject player : players) {
            usernames.add(player.username());
            usernameUuidMap.put(player.username().toLowerCase(), player.uuid());
            uuidUsernameMap.put(player.uuid(), player.username());
        }
    }

    public static boolean checkPlayerByUsername(String username) {
        return usernames.contains(username);
    }
    /*
    public static PlayerDatabaseObject getPlayerByUsername(String username) {
        if (!usernameUuidMap.containsKey(username.toLowerCase())) return null;
        return new PlayerDatabaseObject()
    }
    */
}
