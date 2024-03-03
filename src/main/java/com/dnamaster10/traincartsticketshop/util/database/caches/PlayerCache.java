package com.dnamaster10.traincartsticketshop.util.database.caches;

import com.dnamaster10.traincartsticketshop.util.database.AccessorFactory;
import com.dnamaster10.traincartsticketshop.util.database.accessorinterfaces.PlayerAccessor;
import com.dnamaster10.traincartsticketshop.util.database.databaseobjects.PlayerDatabaseObject;
import com.dnamaster10.traincartsticketshop.util.exceptions.QueryException;

import java.util.HashMap;
import java.util.List;

public class PlayerCache {
    private static final HashMap<String, PlayerDatabaseObject> usernamePlayerMap = new HashMap<>();
    private static final HashMap<String, PlayerDatabaseObject> uuidPlayerMap = new HashMap<>();

    public static void initialize() throws QueryException {
        PlayerAccessor playerAccessor = AccessorFactory.getPlayerAccessor();

        List<PlayerDatabaseObject> players = playerAccessor.getAllPlayers();
        for (PlayerDatabaseObject player : players) {
            usernamePlayerMap.put(player.username().toLowerCase(), player);
            uuidPlayerMap.put(player.uuid(), player);
        }
    }

    public static boolean checkPlayerByUsername(String username) {
        return usernamePlayerMap.containsKey(username.toLowerCase());
    }

    public static PlayerDatabaseObject getPlayerByUsername(String username) {
        return usernamePlayerMap.get(username.toLowerCase());
    }

    public static void updatePlayer(PlayerDatabaseObject player) {
        uuidPlayerMap.put(player.uuid(), player);
        usernamePlayerMap.put(player.username().toLowerCase(), player);
    }
}
