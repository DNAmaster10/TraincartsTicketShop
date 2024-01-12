package com.dnamaster10.tcgui.util.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
            return (total > 0);
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
    public void updatePlayer(String name, String uuid) throws SQLException {
        //Updates player information in database
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("UPDATE players SET username=? WHERE uuid=?");
            statement.setString(1, name);
            statement.setString(2, uuid);
            statement.executeUpdate();
        }
    }
    public void addPlayer(String name, String uuid) throws SQLException {
        //Updates a players username from their UUID
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO players (username, uuid) VALUES (?, ?)");
            statement.setString(1, name);
            statement.setString(2, uuid);
            statement.executeUpdate();
        }
    }
}
