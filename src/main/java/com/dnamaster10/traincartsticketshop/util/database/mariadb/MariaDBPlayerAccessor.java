package com.dnamaster10.traincartsticketshop.util.database.mariadb;

import com.dnamaster10.traincartsticketshop.util.database.accessorinterfaces.PlayerAccessor;
import com.dnamaster10.traincartsticketshop.util.database.caches.PlayerCache;
import com.dnamaster10.traincartsticketshop.util.database.databaseobjects.PlayerDatabaseObject;
import com.dnamaster10.traincartsticketshop.util.exceptions.ModificationException;
import com.dnamaster10.traincartsticketshop.util.exceptions.QueryException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MariaDBPlayerAccessor extends MariaDBDatabaseAccessor implements PlayerAccessor {

    public boolean checkPlayerByUsername(String username) throws QueryException {
        //Returns true if a player with the given username exists in database. Case-insensitive
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM players WHERE username=?");
            statement.setString(1, username);
            ResultSet result = statement.executeQuery();
            int total = 0;
            while (result.next()) {
                total = result.getInt(1);
            }
            return total > 0;
        } catch (SQLException e) {
            throw new QueryException(e);
        }
    }

    public List<PlayerDatabaseObject> getAllPlayers() throws QueryException {
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT username,uuid FROM players");
            ResultSet result = statement.executeQuery();
            List<PlayerDatabaseObject> players = new ArrayList<>();
            while (result.next()) {
                players.add(new PlayerDatabaseObject(result.getString("username"), result.getString("uuid")));
            }
            return players;
        } catch (SQLException e) {
            throw new QueryException(e);
        }
    }

    public PlayerDatabaseObject getPlayerByUsername(String name) {
        //Returns player database object from username. Case-insensitive
        return PlayerCache.getPlayerByUsername(name);
    }

    public void updatePlayer(String name, String uuid) throws ModificationException {
        //Updates or inserts a player into the players table.
        //The last_join column is used in the event that a player changes their username.
        //When selecting UUID from username, if there are duplicate usernames, the plugin will favour the most
        //recently joined player.
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("""
                    INSERT INTO players (username, uuid, last_join)
                    VALUES (?, ?, ?)
                    ON DUPLICATE KEY UPDATE
                        username=VALUES(username),
                        last_join=VALUES(last_join)
                    """);
            statement.setString(1, name);
            statement.setString(2, uuid);
            statement.setLong(3, System.currentTimeMillis());
            statement.executeUpdate();

            //Update database cache
            PlayerCache.updatePlayer(new PlayerDatabaseObject(name, uuid));
        } catch (SQLException e) {
            throw new ModificationException(e);
        }
    }
}
