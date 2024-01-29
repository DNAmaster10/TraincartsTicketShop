package com.dnamaster10.tcgui.util.database;

import com.dnamaster10.tcgui.util.database.databaseobjects.TicketDatabaseObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.dnamaster10.tcgui.TraincartsGui.getPlugin;

public class TicketAccessor extends DatabaseAccessor {
    public TicketAccessor() throws SQLException {
        super();
    }
    public TicketDatabaseObject[] getTickets(int guiId, int page) throws SQLException {
        //Returns an array of ticket database objects from the database
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT slot, tc_name, display_name, raw_display_name, price FROM tickets WHERE guiid=? AND page=?");
            statement.setInt(1, guiId);
            statement.setInt(2, page);
            ResultSet result = statement.executeQuery();
            List<TicketDatabaseObject> ticketList = new ArrayList<>();
            while (result.next()) {
                ticketList.add(new TicketDatabaseObject(result.getInt("slot"), result.getString("tc_name"), result.getString("display_name"), result.getString("raw_display_name"), result.getInt("price")));
            }
            return ticketList.toArray(TicketDatabaseObject[]::new);
        }
    }
    public TicketDatabaseObject[] searchTickets(int guiId, int offset, String searchTerm) throws SQLException {
        //Takes in an offset value and a search term. The method will do a search for ticket names which start with the search term.
        //Due to the limited size of minecraft double chests, an offset value is used.
        //This value indicates the amount of database results which will be skipped over before returning any results.
        //This can be used to have multi-page search guis.
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT tc_name, display_name, raw_display_name, price FROM tickets WHERE guiid=? AND raw_display_name LIKE ? ORDER BY raw_display_name LIMIT 45 OFFSET ?");
            statement.setInt(1, guiId);
            statement.setString(2, searchTerm + "%");
            statement.setInt(3, offset);
            ResultSet result = statement.executeQuery();
            List<TicketDatabaseObject> ticketList = new ArrayList<>();
            int i = 0;
            while (result.next()) {
                ticketList.add(new TicketDatabaseObject(i, result.getString("tc_name"), result.getString("display_name"), result.getString("raw_display_name"), result.getInt("price")));
                i++;
            }
            return ticketList.toArray(TicketDatabaseObject[]::new);
        }
    }
    public int getTotalTicketSearchResults(int guiId, String searchTerm) throws SQLException {
        //Returns total search results which were found
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM tickets WHERE guiid=? AND raw_display_name LIKE ?");
            statement.setInt(1, guiId);
            statement.setString(2, searchTerm + "%");
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return result.getInt(1);
            }
            return 0;
        }
    }
    public void addTickets(int guiId, int page, List<TicketDatabaseObject> tickets) throws SQLException {
        //Batch inserts a list of tickets. This more performant than multiple inset statements.
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO tickets (guiid, page, slot, tc_name, display_name, raw_display_name, price) VALUES (?, ?, ?, ?, ?, ?, ?)");
            for (TicketDatabaseObject ticket : tickets) {
                //Normally for a large batch you would execute every 1000 items or so, but since here there won't (or shouldn't) ever be more than 45 tickets, this isn't
                //needed
                statement.setInt(1, guiId);
                statement.setInt(2, page);
                statement.setInt(3, ticket.getSlot());
                statement.setString(4, ticket.getTcName());
                statement.setString(5, ticket.getColouredDisplayName());
                statement.setString(6, ticket.getRawDisplayName());
                statement.setInt(7, ticket.getPrice());
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }
    public void deleteTicketsByGuid (int guiId) throws SQLException {
        //Deletes tickets registered under a specific gui
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM tickets WHERE guiid=?");
            statement.setInt(1, guiId);
            statement.execute();
        }
    }
    public void deleteTicketsByGuiIdPageId(int guiId, int pageId) throws SQLException {
        //Deleted tickets based on gui id and page id
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM tickets WHERE guiid=? AND page=?");
            statement.setInt(1, guiId);
            statement.setInt(2, pageId);
            statement.execute();
        }
    }
}
