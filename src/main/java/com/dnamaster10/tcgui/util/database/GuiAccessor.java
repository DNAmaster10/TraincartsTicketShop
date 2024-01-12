package com.dnamaster10.tcgui.util.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GuiAccessor extends DatabaseAccessor {
    public GuiAccessor() throws SQLException {
        super();
    }

    public boolean checkGuiByName(String name) throws SQLException {
        //returns true if the gui with the given name exists in database
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM guis WHERE name=?;");
            statement.setString(1, name);
            ResultSet result = statement.executeQuery();
            int total = 0;
            while (result.next()) {
                total = result.getInt(1);
            }
            return (total > 0);
        }
    }
    public boolean checkGuiOwnershipByUuid(String guiName, String ownerUuid) throws SQLException {
        //Returns true if uuid owns gui
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM guis WHERE name=? AND owner_uuid=?");
            statement.setString(1, guiName);
            statement.setString(2, ownerUuid);
            ResultSet result = statement.executeQuery();
            boolean isOwner = false;
            while (result.next()) {
                isOwner = result.getInt(1) > 0;
            }
            return isOwner;
        }
    }
    public boolean checkGuiEditByUuid(String guiName, String uuid) throws SQLException {
        //Get gui ID
        int guiId = getGuiIdByName(guiName);
        //Returns true if player appears in edit table for gui
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM guieditors WHERE guiid=? AND player_uuid=?");
            statement.setInt(1, guiId);
            statement.setString(2, uuid);
            ResultSet result = statement.executeQuery();
            boolean isEditor = false;
            while (result.next()) {
                isEditor = result.getInt(1) > 0;
            }
            return isEditor;
        }
    }
    public Integer getGuiIdByName(String name) throws SQLException {
        //Returns gui id from name
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT id FROM guis WHERE name=?");
            statement.setString(1, name);
            ResultSet result = statement.executeQuery();
            Integer id = null;
            while (result.next()) {
                id = result.getInt(1);
            }
            return id;
        }
    }
    public void addGui(String name, String ownerUuid) throws SQLException {
        //Registers a new GUI in the database
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO guis (name, owner_uuid) VALUES (?,?)");
            statement.setString(1, name);
            statement.setString(2, ownerUuid);
            statement.executeUpdate();
            statement.close();
        }
    }
}
