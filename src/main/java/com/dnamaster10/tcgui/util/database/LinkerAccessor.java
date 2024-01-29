package com.dnamaster10.tcgui.util.database;

import com.dnamaster10.tcgui.util.database.databaseobjects.LinkerDatabaseObject;
import com.dnamaster10.tcgui.util.database.databaseobjects.TicketDatabaseObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LinkerAccessor extends DatabaseAccessor {
    public LinkerAccessor() throws SQLException {
        super();
    }

    public LinkerDatabaseObject[] getLinkersByGuiId(int guiId, int page) throws SQLException {
        //Returns an array of linkers for a given gui ID and page number
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT slot, linked_guiid, display_name, raw_display_name FROM linkers WHERE guiid=? AND page=?");
            statement.setInt(1, guiId);
            statement.setInt(2, page);
            ResultSet result = statement.executeQuery();
            List<LinkerDatabaseObject> linkersList = new ArrayList<>();
            while (result.next()) {
                linkersList.add(new LinkerDatabaseObject(result.getInt("slot"), result.getInt("linked_guiid"), result.getString("display_name"), result.getString("raw_display_name")));
            }
            return linkersList.toArray(LinkerDatabaseObject[]::new);
        }
    }
    public void addLinkers(int guiId, int page, List<LinkerDatabaseObject> linkers) throws SQLException {
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO linkers (guiid, page, slot, linked_guiid, display_name, raw_display_name) VALUES (?, ?, ?, ?, ?, ?)");
            for (LinkerDatabaseObject linker : linkers) {
                statement.setInt(1, guiId);
                statement.setInt(2, page);
                statement.setInt(3, linker.getSlot());
                statement.setInt(4, linker.getLinkedGuiId());
                statement.setString(5, linker.getColouredDisplayName());
                statement.setString(6, linker.getRawDisplayName());
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }
    public void deleteLinkersByGuiId(int guiId) throws SQLException {
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM linkers WHERE guiid=?");
            statement.setInt(1, guiId);
            statement.execute();
        }
    }
}
