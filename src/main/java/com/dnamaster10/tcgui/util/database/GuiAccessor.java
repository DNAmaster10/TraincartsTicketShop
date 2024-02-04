package com.dnamaster10.tcgui.util.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.dnamaster10.tcgui.TraincartsGui.getPlugin;

public class GuiAccessor extends DatabaseAccessor {
    public GuiAccessor() throws SQLException {
        super();
    }

    public boolean checkGuiByName(String name) throws SQLException {
        //returns true if the gui with the given name exists in database
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM guis WHERE name=?");
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
            ResultSet guiResult = statement.executeQuery();
            boolean isOwner = false;
            if (guiResult.next()) {
                isOwner = guiResult.getInt(1) > 0;
            }
            return isOwner;
        }
    }
    public boolean checkGuiEditorByUuid(String guiName, String uuid) throws SQLException {
        //Returns true if player appears in the editor list for the given gui
        try (Connection connection = getConnection()) {
            PreparedStatement statement;

            //Get gui id
            statement = connection.prepareStatement("SELECT id FROM guis WHERE name=?");
            statement.setString(1, guiName);
            ResultSet guiIdResult = statement.executeQuery();
            Integer guiId = null;
            if (guiIdResult.next()) {
                guiId = guiIdResult.getInt(1);
            }
            if (guiId == null) {
                return false;
            }

            //Check if player is an editor of the gui
            statement = connection.prepareStatement("SELECT COUNT(*) FROM guieditors WHERE gui_id=? AND player_uuid=?");
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
        return checkGuiOwnershipByUuid(guiName, ownerUUID) || checkGuiEditorByUuid(guiName, ownerUUID);
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
            PreparedStatement statement;
            int maxPage = 0;

            //Check tickets
            statement = connection.prepareStatement("SELECT MAX(page) FROM tickets WHERE gui_id=?");
            statement.setInt(1, guiId);
            ResultSet ticketsResult = statement.executeQuery();
            if (ticketsResult.next()) {
                maxPage = ticketsResult.getInt(1);
            }

            //Check linkers
            statement = connection.prepareStatement("SELECT MAX(page) FROM linkers WHERE gui_id=?");
            statement.setInt(1, guiId);
            ResultSet linkersResult = statement.executeQuery();
            int linkersPages = 0;
            if (linkersResult.next()) {
                linkersPages = linkersResult.getInt(1);
            }
            if (linkersPages > maxPage) {
                maxPage = linkersPages;
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
            PreparedStatement statement = connection.prepareStatement("INSERT INTO guieditors (gui_id, player_uuid) VALUES (?, ?)");
            statement.setInt(1, guiId);
            statement.setString(2, uuid);
            statement.executeUpdate();
        }
    }
    public void insertPage(int guiId, int currentPage) throws SQLException {
        //Inserts a page above the current page
        try(Connection connection = getConnection()) {
            PreparedStatement statement;

            //Note that order by is required here to avoid duplicate composite key errors as the query works up the page number
            //as opposed to down.

            //Increment tickets page
            statement = connection.prepareStatement("UPDATE tickets SET page = page + 1 WHERE gui_id=? AND page >= ? ORDER BY page DESC");
            statement.setInt(1, guiId);
            statement.setInt(2, currentPage);
            statement.executeUpdate();

            //Increment linkers page
            statement = connection.prepareStatement("UPDATE linkers SET page = page + 1 WHERE gui_id=? AND page >= ? ORDER BY page DESC");
            statement.setInt(1, guiId);
            statement.setInt(2, currentPage);
            statement.executeUpdate();
        }
    }
    public void deleteGuiById(int id) throws SQLException {
        //Deletes a gui by its id
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM guis WHERE id=?");
            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }
    public void deletePage(int guiId, int page) throws SQLException {
        //Deletes the given page from a gui. Deletes tickets and linkers too.
        try (Connection connection = getConnection()) {
            PreparedStatement statement;

            //Remove page items
            statement = connection.prepareStatement("DELETE FROM tickets WHERE gui_id=? AND page=?");
            statement.setInt(1, guiId);
            statement.setInt(2, page);
            statement.addBatch();
            statement.executeUpdate();

            statement = connection.prepareStatement("DELETE FROM linkers WHERE gui_id=? AND page=?");
            statement.setInt(1, guiId);
            statement.setInt(2, page);
            statement.addBatch();
            statement.executeUpdate();

            //Decrement item pages for items above this page
            statement = connection.prepareStatement("UPDATE tickets SET page = page - 1 WHERE gui_id=? AND page >= ?");
            statement.setInt(1, guiId);
            statement.setInt(2, page);
            statement.addBatch();
            statement.executeUpdate();

            statement = connection.prepareStatement("UPDATE linkers SET page = page - 1 WHERE gui_id=? AND page >= ?");
            statement.setInt(1, guiId);
            statement.setInt(2, page);
            statement.addBatch();
            statement.executeUpdate();
        }
    }
    public void removeGuiEditorByUuid(int guiId, String uuid) throws SQLException {
        //Removes a player as editor from gui editors
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM guieditors WHERE gui_id=? AND player_uuid=?");
            statement.setInt(1, guiId);
            statement.setString(2, uuid);
            statement.execute();
        }
    }
    public void removeAllGuiEditors(int guiId) throws SQLException {
        //Removes all editors from a given gui
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM guieditors WHERE gui_id=?");
            statement.setInt(1, guiId);
            statement.execute();
        }
    }
}
