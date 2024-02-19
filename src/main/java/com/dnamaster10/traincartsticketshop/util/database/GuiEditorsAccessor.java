package com.dnamaster10.traincartsticketshop.util.database;

import com.dnamaster10.traincartsticketshop.util.database.databaseobjects.PlayerDatabaseObject;
import com.dnamaster10.traincartsticketshop.util.exceptions.DMLException;
import com.dnamaster10.traincartsticketshop.util.exceptions.DQLException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GuiEditorsAccessor extends DatabaseAccessor {
    public GuiEditorsAccessor() throws DQLException {
        super();
    }

    public boolean checkGuiEditorByUuid(int guiId, String uuid) throws DQLException {
        //Returns true if uuid appears in gui edit list for the given gui id
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM guieditors WHERE gui_id=? AND editor_uuid=?");
            statement.setInt(1, guiId);
            statement.setString(2, uuid);
            ResultSet result = statement.executeQuery();
            boolean isEditor = false;
            if (result.next()) {
                isEditor = result.getInt(1) > 0;
            }
            return isEditor;
        } catch (SQLException e) {
            throw new DQLException(e);
        }
    }

    public int getTotalEditors(int guiId) throws DQLException {
        //Returns total number of editors for a gui
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM guieditors WHERE gui_id=?");
            statement.setInt(1, guiId);
            ResultSet result = statement.executeQuery();
            int total = 0;
            if (result.next()) {
                total = result.getInt(1);
            }
            return total;
        } catch (SQLException e) {
            throw new DQLException(e);
        }
    }

    public String[] getEditorUsernames(int guiId, int startIndex, int limit) throws DQLException {
        //Returns a list of username of players who are a registered editor of a gui
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("""
                    SELECT players.username
                    FROM guieditors
                    INNER JOIN players ON guieditors.editor_uuid=players.uuid
                    WHERE guieditors.gui_id = ?
                    ORDER BY players.username ASC
                    LIMIT ?,?                    
                    """);
            statement.setInt(1, guiId);
            statement.setInt(2, startIndex);
            statement.setInt(3, limit);
            ResultSet result = statement.executeQuery();
            List<String> editorList = new ArrayList<>();
            while (result.next()) {
                editorList.add(result.getString("players.username"));
            }
            return editorList.toArray(String[]::new);
        } catch (SQLException e) {
            throw new DQLException(e);
        }
    }

    public void addGuiEditor(int guiId, String uuid) throws DMLException {
        //Adds a player as an editor for the given gui
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement( """
                    INSERT INTO guieditors (gui_id, editor_uuid)
                    VALUES (?, ?)
                    ON DUPLICATE KEY UPDATE
                        gui_id=VALUES(gui_id),
                        editor_uuid=VALUES(editor_uuid)
                    """);
            statement.setInt(1, guiId);
            statement.setString(2, uuid);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DMLException(e);
        }
    }

    public void removeGuiEditor(int guiId, String uuid) throws DMLException {
        //Removes an editor from a gui
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM guieditors WHERE gui_id=? AND editor_uuid=?");
            statement.setInt(1, guiId);
            statement.setString(2, uuid);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DMLException(e);
        }
    }

}
