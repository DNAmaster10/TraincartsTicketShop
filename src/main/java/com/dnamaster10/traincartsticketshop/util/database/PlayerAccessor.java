package com.dnamaster10.traincartsticketshop.util.database;

import com.dnamaster10.traincartsticketshop.util.database.databaseobjects.PlayerDatabaseObject;
import com.dnamaster10.traincartsticketshop.util.exceptions.DMLException;
import com.dnamaster10.traincartsticketshop.util.exceptions.DQLException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PlayerAccessor extends DatabaseAccessor{
    public PlayerAccessor() throws DQLException {
        super();
    }
    public boolean checkPlayerByUsername(String username) throws DQLException {
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
            throw new DQLException(e);
        }
    }
    public PlayerDatabaseObject getPlayerByUsername(String name) throws DQLException {
        //Returns player database object from username. Case-insensitive
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT username,uuid FROM players WHERE username=? ORDER BY last_join DESC LIMIT 1");
            statement.setString(1, name);
            ResultSet result = statement.executeQuery();
            String username = null;
            String uuid = null;
            while (result.next()) {
                username = result.getString("username");
                uuid = result.getString("uuid");
            }
            if (username == null || uuid == null) {
                return null;
            }
            return new PlayerDatabaseObject(username, uuid);
        } catch (SQLException e) {
            throw new DQLException(e);
        }
    }
    public void updatePlayer(String name, String uuid) throws DMLException {
        //TODO change to on duplicate key
        //Updates or inserts a player into the players table.
        //The join date is used in the event that a player changes their username.
        //When selecting UUID from username, if there are duplicate usernames, the plugin will favour the most
        //recently joined player.
        try (Connection connection = getConnection()) {
            PreparedStatement statement;

            //First check if the current UUID already exists. If it does, update
            statement = connection.prepareStatement("SELECT COUNT(*) FROM players WHERE uuid=?");
            statement.setString(1, uuid);
            ResultSet result1 = statement.executeQuery();
            boolean alreadyExists = false;
            while (result1.next()) {
                alreadyExists = result1.getInt(1) > 0;
            }

            if (alreadyExists) {
                //Update current UUID player
                statement = connection.prepareStatement("UPDATE players SET username=?, last_join=? WHERE uuid=?");
            }
            else {
                //Add the new player
                statement = connection.prepareStatement("INSERT INTO players (username, last_join, uuid) VALUES (?, ?, ?)");
            }
            statement.setString(1, name);
            statement.setLong(2, System.currentTimeMillis());
            statement.setString(3, uuid);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DMLException(e);
        }
    }
}
