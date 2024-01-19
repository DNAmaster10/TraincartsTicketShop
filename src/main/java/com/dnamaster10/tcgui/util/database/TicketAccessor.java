package com.dnamaster10.tcgui.util.database;

import com.dnamaster10.tcgui.util.database.databaseobjects.TicketDatabaseObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TicketAccessor extends DatabaseAccessor {
    public TicketAccessor() throws SQLException {
        super();
    }
    public TicketDatabaseObject[] getTickets(int guiid, int page) throws SQLException {
        //Returns an array of ticket database objects from the database
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT slot, tc_name, display_name, price FROM tickets WHERE guiid=? AND page=?");
            statement.setInt(1, guiid);
            statement.setInt(2, page);
            ResultSet result = statement.executeQuery();
            List<TicketDatabaseObject> ticketList = new ArrayList<>();
            while (result.next()) {
                ticketList.add(new TicketDatabaseObject(result.getInt("slot"), result.getString("tc_name"), result.getString("display_name"), result.getInt("price")));
            }
            return ticketList.toArray(TicketDatabaseObject[]::new);
        }
    }
    public void addTickets(int guiId, int page, List<TicketDatabaseObject> tickets) throws SQLException {
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO tickets (guiid, page, slot, tc_name, display_name, price) VALUES (?, ?, ?, ?, ?, ?)");
            for (TicketDatabaseObject ticket : tickets) {
                //Normally for a large batch you would execute every 1000 items or so, but since here there won't (or shouldn't) ever be more than around 50 tickets, this isn't
                //needed
                statement.setInt(1, guiId);
                statement.setInt(2, page);
                statement.setInt(3, ticket.getSlot());
                statement.setString(4, ticket.getTcName());
                statement.setString(5, ticket.getDisplayName());
                statement.setInt(6, ticket.getPrice());
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
    public void deleteTicketsBYGuiIdPageId(int guiId, int pageId) throws SQLException {
        //Deleted tickets based on gui id and page id
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM tickets WHERE guiid=? AND page=?");
            statement.setInt(1, guiId);
            statement.setInt(2, pageId);
            statement.execute();
        }
    }
}
