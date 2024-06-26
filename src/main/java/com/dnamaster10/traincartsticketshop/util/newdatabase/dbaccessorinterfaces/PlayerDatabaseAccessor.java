package com.dnamaster10.traincartsticketshop.util.newdatabase.dbaccessorinterfaces;

import com.dnamaster10.traincartsticketshop.util.newdatabase.databaseobjects.PlayerDatabaseObject;
import com.dnamaster10.traincartsticketshop.util.exceptions.ModificationException;
import com.dnamaster10.traincartsticketshop.util.exceptions.QueryException;

import java.util.List;

public interface PlayerDatabaseAccessor {
    List<PlayerDatabaseObject> getAllPlayers() throws QueryException;
    void updatePlayer(String username, String uuid) throws ModificationException;
}
