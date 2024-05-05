package com.dnamaster10.traincartsticketshop.util.database.accessorinterfaces;

import com.dnamaster10.traincartsticketshop.util.database.DatabaseAccessor;
import com.dnamaster10.traincartsticketshop.util.database.databaseobjects.PlayerDatabaseObject;
import com.dnamaster10.traincartsticketshop.util.exceptions.ModificationException;
import com.dnamaster10.traincartsticketshop.util.exceptions.QueryException;

import java.util.List;

public abstract class PlayerAccessor extends DatabaseAccessor {
    public boolean checkPlayerByUsername(String username) {
        return getPlayerCache().checkPlayerByUsername(username);
    }

    public abstract List<PlayerDatabaseObject> getAllPlayersFromDatabase() throws QueryException;
    PlayerDatabaseObject getPlayerByUsername(String username);
    PlayerDatabaseObject getPlayerByUuid(String uuid);
    List<String> getPartialUsernameMatches(String inputString);

    void updatePlayer(String name, String uuid) throws ModificationException;
}
