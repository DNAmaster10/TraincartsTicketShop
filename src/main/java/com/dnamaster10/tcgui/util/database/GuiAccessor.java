package com.dnamaster10.tcgui.util.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
    public boolean playerCanEdit(String guiName, String ownerUUID) throws SQLException {
        //Returns true if player is either an owner of a gui or a listed editor
        int guiId = getGuiIdByName(guiName);
        return checkGuiOwnershipByUuid(guiName, ownerUUID) || checkGuiEditByUuid(guiName, ownerUUID);
    }
    public List<String> getEditorsUuids(int guiId) throws SQLException {
        //Returns a list of player UUIDs who have permission to edit a specific gui
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT player_uuid FROM guieditors WHERE guiid=?");
            statement.setInt(1, guiId);
            ResultSet result = statement.executeQuery();
            List<String> uuids = new ArrayList<>();
            while (result.next()) {
                uuids.add(result.getString("player_uuid"));
            }
            return uuids;
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
    public Integer getTotalPages(int guiId) throws SQLException {
        //Returns the total pages for this giu
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT MAX(page) FROM tickets WHERE guiid=?");
            statement.setInt(1, guiId);
            ResultSet result = statement.executeQuery();
            Integer maxPage = null;
            while (result.next()) {
                maxPage = result.getInt(1);
            }
            return maxPage;
        }
    }
    public String getGuiDisplayName(String guiName) throws SQLException {
        //Returns display name of gui from name
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT display_name FROM guis WHERE name=?");
            statement.setString(1, guiName);
            ResultSet result = statement.executeQuery();
            String displayName = null;
            while (result.next()) {
                displayName = result.getString("display_name");
            }
            return displayName;
        }
    }
    public void updateGuiName(String oldName, String newName) throws SQLException {
        //Renames a gui in the database
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("UPDATE guis SET name=? WHERE name=?");
            statement.setString(1, oldName);
            statement.setString(2, newName);
            statement.executeUpdate();
        }
    }
    public void addGui(String name, String ownerUuid) throws SQLException {
        //Registers a new GUI in the database
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO guis (name, owner_uuid) VALUES (?,?)");
            statement.setString(1, name);
            statement.setString(2, ownerUuid);
            statement.executeUpdate();
        }
    }
    public void deleteGuiById(int id) throws SQLException {
        //Deletes a gui by its id
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM guis WHERE id=?");
            statement.setInt(1, id);
            statement.execute();
        }
    }
}
