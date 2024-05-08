package com.dnamaster10.traincartsticketshop.util.newdatabase.accessors;

import com.dnamaster10.traincartsticketshop.util.newdatabase.databaseobjects.PlayerDatabaseObject;
import com.dnamaster10.traincartsticketshop.util.exceptions.ModificationException;
import com.dnamaster10.traincartsticketshop.util.exceptions.QueryException;
import com.dnamaster10.traincartsticketshop.util.newdatabase.DatabaseAccessorFactory;
import com.dnamaster10.traincartsticketshop.util.newdatabase.dbaccessorinterfaces.PlayerDatabaseAccessor;

import java.util.List;

public class PlayerDataAccessor extends DataAccessor {
    private final PlayerDatabaseAccessor playerDatabaseAccessor = DatabaseAccessorFactory.getPlayerDatabaseAccessor();

    public boolean checkPlayerByUsername(String username) {
        return getPlayerCache().checkPlayerByUsername(username);
    }

    public List<PlayerDatabaseObject> getAllPlayersFromDatabase() throws QueryException {
        return playerDatabaseAccessor.getAllPlayers();
    }
    public PlayerDatabaseObject getPlayerByUsername(String username) {
        return getPlayerCache().getPlayerByUsername(username);
    }
    public PlayerDatabaseObject getPlayerByUuid(String uuid) {
        return getPlayerCache().getPlayerByUuid(uuid);
    }
    public List<String> getPartialUsernameMatches(String inputString) {
        return getPlayerCache().getPartialUsernameMatches(inputString);
    }

    public void updatePlayer(String username, String uuid) throws ModificationException {
        getPlayerCache().updatePlayer(username, uuid);
        playerDatabaseAccessor.updatePlayer(username, uuid);
    }
}
