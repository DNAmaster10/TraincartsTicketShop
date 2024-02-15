package com.dnamaster10.traincartsticketshop.util.database;

import com.dnamaster10.traincartsticketshop.util.exceptions.DMLException;
import com.dnamaster10.traincartsticketshop.util.exceptions.DQLException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GuiEditorsAccessor extends DatabaseAccessor {
    public GuiEditorsAccessor() throws DQLException {
        super();
    }

    public boolean checkGuiEditorByUuid(int guiId, String uuid) throws DQLException {
        //Returns true if uuid appears in gui edit list for the given gui id
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM guieditors WHERE gui_id=? AND player_uuid=?");
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

    public void addGuiEditor(int guiId, String uuid) throws DMLException {
        //Adds a player as an editor for the given gui
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement( """
                    INSERT INTO guieditors (gui_id, player_uuid) VALUES (?,?) 
                    VALUES (?, ?)
                    ON DUPLICATE KEY UPDATE
                        gui_id=VALUES(gui_id)
                        player_uuid=VALUES(player_uuid)
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
            PreparedStatement statement = connection.prepareStatement("DELETE FROM guieditors WHERE gui_id=? AND player_uuid=?");
            statement.setInt(1, guiId);
            statement.setString(2, uuid);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DMLException(e);
        }
    }

}
