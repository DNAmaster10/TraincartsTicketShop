package com.dnamaster10.tcgui.util.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.dnamaster10.tcgui.TraincartsGui.getPlugin;

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
            return total > 0;
        }
    }
    public boolean checkGuiById(int id) throws SQLException {
        //Returns true if the gui with the given name exists in database
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM guis WHERE id=?");
            statement.setInt(1, id);
            ResultSet result = statement.executeQuery();
            int total = 0;
            while (result.next()) {
                total = result.getInt(1);
            }
            return total > 0;
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
    public String getGuiNameById(int id) throws SQLException {
        //Returns gui name from id
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT name FROM guis WHERE id=?");
            statement.setInt(1, id);
            ResultSet result = statement.executeQuery();
            String name = null;
            while (result.next()) {
                name = result.getString("name");
            }
            return name;
        }
    }
    public Integer getMaxPage(int guiId) throws SQLException {
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
    public String getColouredGuiDisplayName(String guiName) throws SQLException {
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
    public void updateGuiDisplayName(String guiName, String colouredDisplayName, String rawDisplayName) throws SQLException {
        //Updates a gui display name
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("UPDATE guis SET display_name=?, raw_display_name=? WHERE name=?");
            statement.setString(1, colouredDisplayName);
            statement.setString(2, rawDisplayName);
            statement.setString(3, guiName);
            statement.executeUpdate();
        }
    }
    public void updateGuiOwner(String guiName, String uuid) throws SQLException {
        //Changes the owner of the gui to another player
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("UPDATE guis SET owner_uuid=? WHERE name=?");
            statement.setString(1, uuid);
            statement.setString(2, guiName);
            statement.executeUpdate();
        }
    }
    public void addGui(String name, String colouredDisplayName, String rawDisplayName, String ownerUuid) throws SQLException {
        //Registers a new GUI in the database
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO guis (name, display_name, raw_display_name, owner_uuid) VALUES (?, ?, ?, ?)");
            statement.setString(1, name);
            statement.setString(2, colouredDisplayName);
            statement.setString(3, rawDisplayName);
            statement.setString(4, ownerUuid);
            statement.executeUpdate();
        }
    }
    public void addGuiEditor(String uuid, int guiId) throws SQLException {
        //Adds an editor for a given gui
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO guieditors (guiid, player_uuid) VALUES (?, ?)");
            statement.setInt(1, guiId);
            statement.setString(2, uuid);
            statement.executeUpdate();
        }
    }
    public void insertPage(int guiId, int currentPage) throws SQLException {
        //Inserts a page above the current page
        try(Connection connection = getConnection()) {
            PreparedStatement statement;

            //Increment tickets page
            statement = connection.prepareStatement("UPDATE tickets SET page = page + 1 WHERE guiid=? AND page > ?");
            statement.setInt(1, guiId);
            statement.setInt(2, currentPage);
            statement.executeUpdate();

            //Increment linkers page
            statement = connection.prepareStatement("UPDATE linkers SET page = page + 1 WHERE guiid=? AND page > ?");
            statement.setInt(1, guiId);
            statement.setInt(2, currentPage);
            statement.executeUpdate();
        }
    }
    public void deleteGuiById(int id) throws SQLException {
        //Deletes a gui by its id
        try (Connection connection = getConnection()) {
            PreparedStatement statement;

            statement = connection.prepareStatement("DELETE FROM guis WHERE id=?");
            statement.setInt(1, id);
            statement.executeUpdate();

            statement = connection.prepareStatement("DELETE FROM tickets WHERE guiid=?");
            statement.setInt(1, id);
            statement.executeUpdate();

            statement = connection.prepareStatement("DELETE FROM linkers WHERE guiid=?");
            statement.setInt(1, id);
            statement.executeUpdate();

            statement = connection.prepareStatement("DELETE FROM guieditors WHERE guiid=?");
            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }
    public void deletePage(int guiId, int page) throws SQLException {
        //Deletes the given page from a gui. Deletes tickets and linkers too.
        try (Connection connection = getConnection()) {
            PreparedStatement statement;
            
            statement = connection.prepareStatement("DELETE FROM tickets WHERE guiid=? AND page=?");
            statement.setInt(1, guiId);
            statement.setInt(2, page);
            statement.addBatch();
            statement.executeUpdate();

            statement = connection.prepareStatement("DELETE FROM linkers WHERE guiid=? AND page=?");
            statement.setInt(1, guiId);
            statement.setInt(2, page);
            statement.addBatch();
            statement.executeUpdate();

            statement = connection.prepareStatement("UPDATE tickets SET page = page - 1 WHERE guiid=? AND page > ?");
            statement.setInt(1, guiId);
            statement.setInt(2, page);
            statement.addBatch();
            statement.executeUpdate();

            statement = connection.prepareStatement("UPDATE linkers SET page = page - 1 WHERE guiid=? AND page > ?");
            statement.setInt(1, guiId);
            statement.setInt(2, page);
            statement.addBatch();
            statement.executeUpdate();
        }
    }
    public void removeGuiEditorByUuid(int guiId, String uuid) throws SQLException {
        //Removes a player as editor from gui editors
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM guieditors WHERE guiid=? AND player_uuid=?");
            statement.setInt(1, guiId);
            statement.setString(2, uuid);
            statement.execute();
        }
    }
    public void removeAllGuiEditors(int guiId) throws SQLException {
        //Removes all editors from a given gui
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM guieditors WHERE guiid=?");
            statement.setInt(1, guiId);
            statement.execute();
        }
    }
}
