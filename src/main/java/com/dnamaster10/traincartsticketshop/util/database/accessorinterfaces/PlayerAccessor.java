package com.dnamaster10.traincartsticketshop.util.database.accessorinterfaces;

import com.dnamaster10.traincartsticketshop.util.database.databaseobjects.PlayerDatabaseObject;
import com.dnamaster10.traincartsticketshop.util.exceptions.ModificationException;
import com.dnamaster10.traincartsticketshop.util.exceptions.QueryException;

public interface PlayerAccessor {
    boolean checkPlayerByUsername(String username) throws QueryException;
    PlayerDatabaseObject getPlayerByUsername(String username) throws QueryException;
    void updatePlayer(String name, String uuid) throws ModificationException;
}
