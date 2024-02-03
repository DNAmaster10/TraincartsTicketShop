package com.dnamaster10.tcgui.util.database;

import com.dnamaster10.tcgui.util.database.databaseobjects.PlayerDatabaseObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PlayerAccessor extends DatabaseAccessor{
    public PlayerAccessor() throws SQLException {
        super();
    }

    public boolean checkPlayerByUuid(String uuid) throws SQLException {
        //Returns true if a player exists in database
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM players WHERE uuid=?");
            statement.setString(1, uuid);
            ResultSet result = statement.executeQuery();
            int total = 0;
            while (result.next()) {
                total = result.getInt(1);
            }
            return total > 0;
        }
    }
    public boolean checkPlayerByUsername(String username) throws SQLException {
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
        }
    }
    public String getUsernameFromUuid(String uuid) throws SQLException {
        //Returns player name from UUID as string
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT username FROM players WHERE uuid=?");
            statement.setString(1, uuid);
            ResultSet result = statement.executeQuery();
            String name = null;
            while (result.next()) {
                name = result.getString("username");
            }
            return name;
        }
    }
    public List<String> getUsernamesFromUuids(List<String> uuids) throws SQLException {
        //Takes a list of UUIDs and returns a list of usernames
        try (Connection connection = getConnection()) {
            //Build the SQL statement
            StringBuilder sql = new StringBuilder("SELECT username FROM players WHERE uuid IN (");
            sql.append("?, ".repeat(uuids.size()));
            //Delete last comma
            sql.delete(sql.length()-1, sql.length());
            sql.append(") ORDER BY username");

            //Assign parameters
            PreparedStatement s = connection.prepareStatement(sql.toString());
            for (int i = 0; i < uuids.size(); i++) {
                s.setString(i + 1, uuids.get(i));
            }

            ResultSet result = s.executeQuery();
            List<String> usernames = new ArrayList<>();
            while (result.next()) {
                usernames.add(result.getString("username"));
            }
            return usernames;
        }
    }
    public PlayerDatabaseObject getPlayerByUsername(String name) throws SQLException {
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
        }
    }
    public void updatePlayer(String name, String uuid) throws SQLException {
        //Updates or inserts a player into the players table.
        //The join date is used in the event that a player changes their username.
        //When selecting UUID from username, if there are duplicate usernames, the plugin will favour the most
        //recently joined player.
        try (Connection connection = getConnection()) {
            int lastJoin = (int) System.currentTimeMillis();
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
                statement.setString(1, name);
                statement.setInt(2, lastJoin);
                statement.setString(3, uuid);
                statement.executeUpdate();
            }
            else {
                //Add the new player
                statement = connection.prepareStatement("INSERT INTO players (username, last_join, uuid) VALUES (?, ?, ?)");
                statement.setString(1, name);
                statement.setInt(2, lastJoin);
                statement.setString(3, uuid);
                statement.executeUpdate();
            }
        }
    }
}
