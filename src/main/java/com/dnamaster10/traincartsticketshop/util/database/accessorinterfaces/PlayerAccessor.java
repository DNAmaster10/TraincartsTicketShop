package com.dnamaster10.traincartsticketshop.util.database.accessorinterfaces;

import com.dnamaster10.traincartsticketshop.util.database.databaseobjects.PlayerDatabaseObject;
import com.dnamaster10.traincartsticketshop.util.exceptions.ModificationException;
import com.dnamaster10.traincartsticketshop.util.exceptions.QueryException;

import java.util.List;

public interface PlayerAccessor {
    boolean checkPlayerByUsername(String username) throws QueryException;

    List<PlayerDatabaseObject> getAllPlayers() throws QueryException;
    PlayerDatabaseObject getPlayerByUsername(String username) throws QueryException;
    void updatePlayer(String name, String uuid) throws ModificationException;
}
