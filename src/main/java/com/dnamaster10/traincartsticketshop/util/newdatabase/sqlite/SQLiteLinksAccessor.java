package com.dnamaster10.traincartsticketshop.util.newdatabase.sqlite;

import com.dnamaster10.traincartsticketshop.util.database.databaseobjects.LinkDatabaseObject;
import com.dnamaster10.traincartsticketshop.util.exceptions.ModificationException;
import com.dnamaster10.traincartsticketshop.util.exceptions.QueryException;
import com.dnamaster10.traincartsticketshop.util.newdatabase.dbaccessorinterfaces.LinksDatabaseAccessor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class SQLiteLinksAccessor extends SQLiteDatabaseAccessor implements LinksDatabaseAccessor {
    @Override
    public LinkDatabaseObject[] getLinksByGuiId(int guiId, int page) throws QueryException {
        return new LinkDatabaseObject[0];
    }

    @Override
    public LinkDatabaseObject[] searchLinks(int guiId, int offset, String searchTerm) throws QueryException {
        return new LinkDatabaseObject[0];
    }

    @Override
    public int getTotalLinks(int guiId) throws QueryException {
        return 0;
    }

    @Override
    public int getTotalLinkSearchResults(int guiId, String searchTerm) throws QueryException {
        return 0;
    }

    @Override
    public void saveLinkPage(int guiId, int page, List<LinkDatabaseObject> links) throws ModificationException {
        try (Connection connection = getConnection()) {
            //Delete existing page
            PreparedStatement statement = connection.prepareStatement("DELETE FROM links WHERE gui_id=? AND page=?");
            statement.setInt(1, guiId);
            statement.setInt(2, page);
            statement.executeUpdate();

            if (links.isEmpty()) return;

            //Add new items
            statement = connection.prepareStatement("""
                    INSERT INTO links
                    (gui_id, page, slot, linked_gui_id, linked_gui_page, display_name, raw_display_name)
                    VALUES (?, ?, ?, ?, ?, ?, ?)
                    """);
            for (LinkDatabaseObject link : links) {
                statement.setInt(1, guiId);
                statement.setInt(2, page);
                statement.setInt(3, link.slot());
                statement.setInt(4, link.linkedGuiId());
                statement.setInt(5, link.linkedGuiPage());
                statement.setString(6, link.colouredDisplayName());
                statement.setString(7, link.rawDisplayName());

                statement.addBatch();
            }
            statement.executeBatch();
        } catch (SQLException e) {
            throw new ModificationException(e);
        }
    }
}
