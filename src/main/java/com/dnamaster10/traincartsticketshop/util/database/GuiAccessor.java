package com.dnamaster10.traincartsticketshop.util.database;

import com.dnamaster10.traincartsticketshop.util.exceptions.DMLException;
import com.dnamaster10.traincartsticketshop.util.exceptions.DQLException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.dnamaster10.traincartsticketshop.TraincartsTicketShop.getPlugin;

public class GuiAccessor extends DatabaseAccessor {
    public GuiAccessor() throws DQLException {
        super();
    }

    public boolean checkGuiByName(String name) throws DQLException {
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
        } catch (SQLException e) {
            throw new DQLException(e);
        }
    }
    public boolean checkGuiById(int id) throws DQLException {
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
        } catch (SQLException e) {
            throw new DQLException(e);
        }
    }
    public boolean checkGuiOwnershipByUuid(int guiId, String ownerUuid) throws DQLException {
        //Returns true if uuid owns gui
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM guis WHERE id=? AND owner_uuid=?");
            statement.setInt(1, guiId);
            statement.setString(2, ownerUuid);
            ResultSet guiResult = statement.executeQuery();
            boolean isOwner = false;
            if (guiResult.next()) {
                isOwner = guiResult.getInt(1) > 0;
            }
            return isOwner;
        } catch (SQLException e) {
            throw new DQLException(e);
        }
    }
    public boolean checkGuiEditorByUuid(int guiId, String uuid) throws DQLException {
        //Returns true if player appears in the editor list for the given gui
        try (Connection connection = getConnection()) {
            PreparedStatement statement;

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
        } catch (SQLException e) {
            throw new DQLException(e);
        }
    }
    public boolean playerCanEdit(int guiId, String ownerUUID) throws DQLException {
        //Returns true if player is either an owner of a gui or a listed editor
        return checkGuiOwnershipByUuid(guiId, ownerUUID) || checkGuiEditorByUuid(guiId, ownerUUID);
    }

    public Integer getGuiIdByName(String name) throws DQLException {
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
        } catch (SQLException e) {
            throw new DQLException(e);
        }
    }
    public String getGuiNameById(int id) throws DQLException {
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
        } catch (SQLException e) {
            throw new DQLException(e);
        }
    }
    public Integer getMaxPage(int guiId) throws DQLException {
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
        } catch (SQLException e) {
            throw new DQLException(e);
        }
    }
    public String getColouredDisplayNameById(int guiId) throws DQLException {
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT display_name FROM guis WHERE id=?");
            statement.setInt(1, guiId);
            ResultSet result = statement.executeQuery();
            String displayName = null;
            if (result.next()) {
                displayName = result.getString("display_name");
            }
            return displayName;
        } catch (SQLException e) {
            throw new DQLException(e);
        }
    }
    public void updateGuiName(String oldName, String newName) throws DMLException {
        //Renames a gui in the database
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("UPDATE guis SET name=? WHERE name=?");
            statement.setString(1, oldName);
            statement.setString(2, newName);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DMLException(e);
        }
    }
    public void updateGuiDisplayName(String guiName, String colouredDisplayName, String rawDisplayName) throws DMLException {
        //Updates a gui display name
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("UPDATE guis SET display_name=?, raw_display_name=? WHERE name=?");
            statement.setString(1, colouredDisplayName);
            statement.setString(2, rawDisplayName);
            statement.setString(3, guiName);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DMLException(e);
        }
    }
    public void updateGuiOwner(String guiName, String uuid) throws DMLException {
        //Changes the owner of the gui to another player
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("UPDATE guis SET owner_uuid=? WHERE name=?");
            statement.setString(1, uuid);
            statement.setString(2, guiName);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DMLException(e);
        }
    }
    public void addGui(String name, String colouredDisplayName, String rawDisplayName, String ownerUuid) throws DMLException {
        //Registers a new GUI in the database
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO guis (name, display_name, raw_display_name, owner_uuid) VALUES (?, ?, ?, ?)");
            statement.setString(1, name);
            statement.setString(2, colouredDisplayName);
            statement.setString(3, rawDisplayName);
            statement.setString(4, ownerUuid);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DMLException(e);
        }
    }
    public void addGuiEditor(String uuid, int guiId) throws DMLException {
        //Adds an editor for a given gui
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO guieditors (gui_id, player_uuid) VALUES (?, ?)");
            statement.setInt(1, guiId);
            statement.setString(2, uuid);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DMLException(e);
        }
    }
    public void insertPage(int guiId, int currentPage) throws DMLException {
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
        } catch (SQLException e) {
            throw new DMLException(e);
        }
    }
    public void deleteGuiById(int id) throws DMLException {
        //Deletes a gui by its id
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM guis WHERE id=?");
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DMLException(e);
        }
    }
    public void deletePage(int guiId, int page) throws DMLException {
        //Deletes the given page from a gui. Deletes tickets and linkers too.
        try (Connection connection = getConnection()) {
            PreparedStatement statement;

            //Remove page items
            statement = connection.prepareStatement("DELETE FROM tickets WHERE gui_id=? AND page=?");
            statement.setInt(1, guiId);
            statement.setInt(2, page);
            statement.executeUpdate();

            statement = connection.prepareStatement("DELETE FROM linkers WHERE gui_id=? AND page=?");
            statement.setInt(1, guiId);
            statement.setInt(2, page);
            statement.executeUpdate();

            //Decrement item pages for items above this page
            //Note that ORDER BY is required to prevent duplicate key error
            statement = connection.prepareStatement("UPDATE tickets SET page = page - 1 WHERE gui_id=? AND page > ? ORDER BY page ASC");
            statement.setInt(1, guiId);
            statement.setInt(2, page);
            statement.executeUpdate();

            statement = connection.prepareStatement("UPDATE linkers SET page = page - 1 WHERE gui_id=? AND page > ? ORDER BY page ASC");
            statement.setInt(1, guiId);
            statement.setInt(2, page);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DMLException(e);
        }
    }
    public void removeGuiEditorByUuid(int guiId, String uuid) throws DMLException {
        //Removes a player as editor from gui editors
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM guieditors WHERE gui_id=? AND player_uuid=?");
            statement.setInt(1, guiId);
            statement.setString(2, uuid);
            statement.execute();
        } catch (SQLException e) {
            throw new DMLException(e);
        }
    }
    public void removeAllGuiEditors(int guiId) throws DMLException {
        //Removes all editors from a given gui
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM guieditors WHERE gui_id=?");
            statement.setInt(1, guiId);
            statement.execute();
        } catch (SQLException e) {
            throw new DMLException(e);
        }
    }
}
