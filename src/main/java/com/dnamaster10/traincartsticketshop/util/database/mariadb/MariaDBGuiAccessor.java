package com.dnamaster10.traincartsticketshop.util.database.mariadb;

import com.dnamaster10.traincartsticketshop.util.database.AccessorFactory;
import com.dnamaster10.traincartsticketshop.util.database.accessorinterfaces.GuiAccessor;
import com.dnamaster10.traincartsticketshop.util.database.accessorinterfaces.GuiEditorsAccessor;
import com.dnamaster10.traincartsticketshop.util.database.databaseobjects.GuiDatabaseObject;
import com.dnamaster10.traincartsticketshop.util.exceptions.ModificationException;
import com.dnamaster10.traincartsticketshop.util.exceptions.QueryException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MariaDBGuiAccessor extends MariaDBDatabaseAccessor implements GuiAccessor {

    public boolean checkGuiByName(String name) {
        return getGuiCache().checkGuiByName(name);
    }
    public boolean checkGuiById(int id) {
        return getGuiCache().checkGuiById(id);
    }
    public boolean checkGuiOwnerByUuid(int guiId, String ownerUuid) throws QueryException {
       return getGuiCache().checkGuiOwnerByUuid(guiId, ownerUuid);
    }
    public boolean playerCanEdit(int guiId, String uuid) throws QueryException {
        //Returns true if player is either an owner of a gui or a listed editor
        if (getGuiCache().checkGuiOwnerByUuid(guiId, uuid)) return true;
        GuiEditorsAccessor guiEditorsAccessor = AccessorFactory.getGuiEditorsAccessor();
        return guiEditorsAccessor.checkGuiEditorByUuid(guiId, uuid);
    }

    public List<GuiDatabaseObject> getGuisFromDatabase() throws QueryException {
        //Returns a list of all guis from database
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT id,name,display_name,owner_uuid FROM guis");
            ResultSet result = statement.executeQuery();
            List<GuiDatabaseObject> guis = new ArrayList<>();
            while (result.next()) {
                GuiDatabaseObject gui = new GuiDatabaseObject(
                        result.getInt("id"),
                        result.getString("name"),
                        result.getString("display_name"),
                        result.getString("owner_uuid")
                );
                guis.add(gui);
            }
            return guis;
        } catch (SQLException e) {
            throw new QueryException(e);
        }
    }
    public Integer getGuiIdByName(String name) throws QueryException {
        //Returns gui id from name
        return getGuiCache().getGuiIdByName(name);
    }
    public String getGuiNameById(int id) throws QueryException {
        //Returns gui name from id
        return getGuiCache().getGuiNameById(id);
    }
    public int getHighestPageNumber(int guiId) throws QueryException {
        //Returns the total pages for this gui
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

            //Check links
            statement = connection.prepareStatement("SELECT MAX(page) FROM links WHERE gui_id=?");
            statement.setInt(1, guiId);
            ResultSet linksResult = statement.executeQuery();
            int linksPages = 0;
            if (linksResult.next()) {
                linksPages = linksResult.getInt(1);
            }
            if (linksPages > maxPage) {
                maxPage = linksPages;
            }

            return maxPage;
        } catch (SQLException e) {
            throw new QueryException(e);
        }
    }
    public String getDisplayNameById(int guiId) throws QueryException {
        return getGuiCache().getDisplayNameById(guiId);
    }
    public String getOwnerUsername(int guiId) throws QueryException {
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("""
                    SELECT players.username
                    FROM guis
                    INNER JOIN players ON guis.owner_uuid=players.uuid
                    WHERE guis.id=?
                    """);
            statement.setInt(1, guiId);
            ResultSet result = statement.executeQuery();
            String ownerUsername = null;
            if (result.next()) {
                ownerUsername = result.getString(1);
            }
            return ownerUsername;
        } catch (SQLException e) {
            throw new QueryException(e);
        }
    }
    public void updateGuiName(int guiId, String newName) throws ModificationException {
        //Renames a gui in the database
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("UPDATE guis SET name=? WHERE id=?");
            statement.setString(1, newName);
            statement.setInt(2, guiId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new ModificationException(e);
        }
        getGuiCache().updateGuiName(guiId, newName);
    }
    public void updateGuiDisplayName(int guiId, String colouredDisplayName, String rawDisplayName) throws ModificationException {
        //Updates a gui display name
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("UPDATE guis SET display_name=?, raw_display_name=? WHERE id=?");
            statement.setString(1, colouredDisplayName);
            statement.setString(2, rawDisplayName);
            statement.setInt(3, guiId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new ModificationException(e);
        }
        getGuiCache().updateGuiDisplayName(guiId, colouredDisplayName);
    }
    public void updateGuiOwner(int guiId, String uuid) throws ModificationException {
        //Changes the owner of the gui to another player
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("UPDATE guis SET owner_uuid=? WHERE id=?");
            statement.setString(1, uuid);
            statement.setInt(2, guiId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new ModificationException(e);
        }
        getGuiCache().updateGuiOwner(guiId, uuid);
    }
    public void addGui(String name, String colouredDisplayName, String rawDisplayName, String ownerUuid) throws ModificationException {
        //Registers a new GUI in the database
        Integer guiId = null;
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO guis (name, display_name, raw_display_name, owner_uuid) VALUES (?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, name);
            statement.setString(2, colouredDisplayName);
            statement.setString(3, rawDisplayName);
            statement.setString(4, ownerUuid);
            statement.executeUpdate();

            //Get the inserted ID
            ResultSet result = statement.getGeneratedKeys();
            if (result.next()) {
                guiId = (int) result.getLong(1);
            }
        } catch (SQLException e) {
            throw new ModificationException(e);
        }

        if (guiId == null) return;

        GuiDatabaseObject newGui = new GuiDatabaseObject(guiId, name, colouredDisplayName, ownerUuid);
        getGuiCache().addGui(newGui);
    }
    public void insertPage(int guiId, int currentPage) throws ModificationException {
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

            //Increment links page
            statement = connection.prepareStatement("UPDATE links SET page = page + 1 WHERE gui_id=? AND page >= ? ORDER BY page DESC");
            statement.setInt(1, guiId);
            statement.setInt(2, currentPage);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new ModificationException(e);
        }
    }
    public void deleteGuiById(int id) throws ModificationException {
        //Deletes a gui by its id
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM guis WHERE id=?");
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new ModificationException(e);
        }
        getGuiCache().deleteGuiById(id);
    }
    public void deletePage(int guiId, int page) throws ModificationException {
        //Deletes the given page from a gui. Deletes tickets and links too.
        try (Connection connection = getConnection()) {
            PreparedStatement statement;

            //Remove page items
            statement = connection.prepareStatement("DELETE FROM tickets WHERE gui_id=? AND page=?");
            statement.setInt(1, guiId);
            statement.setInt(2, page);
            statement.executeUpdate();

            statement = connection.prepareStatement("DELETE FROM links WHERE gui_id=? AND page=?");
            statement.setInt(1, guiId);
            statement.setInt(2, page);
            statement.executeUpdate();

            //Decrement item pages for items above this page
            //Note that ORDER BY is required to prevent duplicate key error
            statement = connection.prepareStatement("UPDATE tickets SET page = page - 1 WHERE gui_id=? AND page > ? ORDER BY page ASC");
            statement.setInt(1, guiId);
            statement.setInt(2, page);
            statement.executeUpdate();

            statement = connection.prepareStatement("UPDATE links SET page = page - 1 WHERE gui_id=? AND page > ? ORDER BY page ASC");
            statement.setInt(1, guiId);
            statement.setInt(2, page);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new ModificationException(e);
        }
    }
}
