package com.dnamaster10.traincartsticketshop.util.database;

import com.dnamaster10.traincartsticketshop.util.database.databaseobjects.LinkerDatabaseObject;
import com.dnamaster10.traincartsticketshop.util.exceptions.DMLException;
import com.dnamaster10.traincartsticketshop.util.exceptions.DQLException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LinkerAccessor extends DatabaseAccessor {
    public LinkerAccessor() throws DQLException {
        super();
    }

    public LinkerDatabaseObject[] getLinkersByGuiId(int guiId, int page) throws DQLException {
        //Returns an array of linkers for a given gui ID and page number
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT slot, linked_gui_id, linked_gui_page, display_name, raw_display_name FROM linkers WHERE gui_id=? AND page=?");
            statement.setInt(1, guiId);
            statement.setInt(2, page);
            ResultSet result = statement.executeQuery();
            List<LinkerDatabaseObject> linkersList = new ArrayList<>();
            while (result.next()) {
                linkersList.add(new LinkerDatabaseObject(result.getInt("slot"), result.getInt("linked_gui_id"), result.getInt("linked_gui_page"), result.getString("display_name"), result.getString("raw_display_name")));
            }
            return linkersList.toArray(LinkerDatabaseObject[]::new);
        } catch (SQLException e) {
            throw new DQLException(e);
        }
    }
    public LinkerDatabaseObject[] searchLinkers(int guiId, int offset, String searchTerm) throws DQLException {
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT linked_gui_id, linked_gui_page, display_name FROM linkers WHERE gui_id=? AND raw_display_name LIKE ? ORDER BY raw_display_name LIMIT 45 OFFSET ?");
            statement.setInt(1, guiId);
            statement.setString(2, searchTerm + "%");
            statement.setInt(3, offset);
            ResultSet result = statement.executeQuery();
            List<LinkerDatabaseObject> linkerList = new ArrayList<>();
            int i = 0;
            while (result.next()) {
                linkerList.add(new LinkerDatabaseObject(i, result.getInt("linked_gui_id"), result.getInt("linked_gui_page"), result.getString("display_name"), null));
                i++;
            }
            return linkerList.toArray(LinkerDatabaseObject[]::new);
        } catch (SQLException e) {
            throw new DQLException(e);
        }
    }
    public int getTotalLinkerSearchResults(int guiId, String searchTerm) throws DQLException {
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM linkers WHERE gui_id=? AND raw_display_name LIKE ?");
            statement.setInt(1, guiId);
            statement.setString(2, searchTerm + "%");
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return result.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new DQLException(e);
        }
    }
    public void saveLinkerPage(int guiId, int page, List<LinkerDatabaseObject> linkers) throws DMLException {
        try (Connection connection = getConnection()) {
            //Delete non-existent slots
            if (linkers.isEmpty()) {
                PreparedStatement deleteStatement = connection.prepareStatement("DELETE FROM linkers WHERE gui_id=? AND page=?");
                deleteStatement.setInt(1, guiId);
                deleteStatement.setInt(2, page);
                deleteStatement.executeUpdate();
                return;
            }
            String sql = "DELETE FROM linkers WHERE gui_id=? AND page=? AND slot NOT IN (";
            StringBuilder placeholders = new StringBuilder();
            for (int i = 0; i < linkers.size(); i++) {
                placeholders.append("?");
                if (i < linkers.size() - 1) {
                    placeholders.append(", ");
                }
            }
            sql += placeholders + ")";
            PreparedStatement deleteStatement = connection.prepareStatement(sql);
            //Set values for placeholders
            deleteStatement.setInt(1, guiId);
            deleteStatement.setInt(2, page);
            for (int i = 0; i < linkers.size(); i++) {
                deleteStatement.setInt(i + 3, linkers.get(i).slot());
            }
            deleteStatement.execute();

            //Prepare update query
            PreparedStatement statement = connection.prepareStatement("""
                    INSERT INTO linkers (gui_id, page, slot, linked_gui_id, linked_gui_page, display_name, raw_display_name) 
                    VALUES (?, ?, ?, ?, ?, ?, ?) 
                    ON DUPLICATE KEY UPDATE 
                        linked_gui_id=VALUES(linked_gui_id),
                        linked_gui_page=VALUES(linked_gui_page),
                        display_name=VALUES(display_name),
                        raw_display_name=VALUES(raw_display_name)
                    """);
            for (LinkerDatabaseObject linker : linkers) {
                statement.setInt(1, guiId);
                statement.setInt(2, page);
                statement.setInt(3, linker.slot());
                statement.setInt(4, linker.linkedGuiId());
                statement.setInt(5, linker.linkedGuiPage());
                statement.setString(6, linker.colouredDisplayName());
                statement.setString(7, linker.rawDisplayName());

                statement.addBatch();
            }
            statement.executeBatch();
        } catch (SQLException e) {
            throw new DMLException(e);
        }
    }
}
