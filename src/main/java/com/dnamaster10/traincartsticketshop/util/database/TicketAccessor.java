package com.dnamaster10.traincartsticketshop.util.database;

import com.dnamaster10.traincartsticketshop.util.database.databaseobjects.TicketDatabaseObject;
import com.dnamaster10.traincartsticketshop.util.exceptions.DMLException;
import com.dnamaster10.traincartsticketshop.util.exceptions.DQLException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TicketAccessor extends DatabaseAccessor {
    public TicketAccessor() throws DQLException {
        super();
    }
    public TicketDatabaseObject[] getTickets(int guiId, int page) throws DQLException {
        //Returns an array of ticket database objects from the database
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT slot, tc_name, display_name, raw_display_name FROM tickets WHERE gui_id=? AND page=?");
            statement.setInt(1, guiId);
            statement.setInt(2, page);
            ResultSet result = statement.executeQuery();
            List<TicketDatabaseObject> ticketList = new ArrayList<>();
            while (result.next()) {
                ticketList.add(new TicketDatabaseObject(result.getInt("slot"), result.getString("tc_name"), result.getString("display_name"), result.getString("raw_display_name")));
            }
            return ticketList.toArray(TicketDatabaseObject[]::new);
        } catch (SQLException e) {
            throw new DQLException(e);
        }
    }
    public TicketDatabaseObject[] searchTickets(int guiId, int offset, String searchTerm) throws DQLException {
        //Takes in an offset value and a search term. The method will do a search for ticket names which start with the search term.
        //Due to the limited size of minecraft double chests, an offset value is used.
        //This value indicates the amount of database results which will be skipped over before returning any results.
        //This can be used to have multi-page search guis.
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT tc_name, display_name, raw_display_name FROM tickets WHERE gui_id=? AND raw_display_name LIKE ? ORDER BY raw_display_name LIMIT 45 OFFSET ?");
            statement.setInt(1, guiId);
            statement.setString(2, searchTerm + "%");
            statement.setInt(3, offset);
            ResultSet result = statement.executeQuery();
            List<TicketDatabaseObject> ticketList = new ArrayList<>();
            int i = 0;
            while (result.next()) {
                ticketList.add(new TicketDatabaseObject(i, result.getString("tc_name"), result.getString("display_name"), result.getString("raw_display_name")));
                i++;
            }
            return ticketList.toArray(TicketDatabaseObject[]::new);
        } catch (SQLException e) {
            throw new DQLException(e);
        }
    }
    public int getTotalTickets(int guiId) throws DQLException {
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM tickets WHERE gui_id=?");
            statement.setInt(1, guiId);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return result.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new DQLException(e);
        }
    }
    public int getTotalTicketSearchResults(int guiId, String searchTerm) throws DQLException {
        //Returns total search results which were found
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM tickets WHERE gui_id=? AND raw_display_name LIKE ?");
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
    public void saveTicketPage(int guiId, int page, List<TicketDatabaseObject> tickets) throws DMLException {
        try (Connection connection = getConnection()) {
            if (tickets.isEmpty()) {
                //If not tickets were provided, delete all existing tickets and return
                PreparedStatement deleteStatement = connection.prepareStatement("DELETE FROM tickets WHERE gui_id=? AND page=?");
                deleteStatement.setInt(1, guiId);
                deleteStatement.setInt(2, page);
                deleteStatement.executeUpdate();
                return;
            }
            //If tickets were provided, remove tickets which do not appear in the list
            String sql = "DELETE FROM tickets WHERE gui_id=? AND page=? AND slot NOT IN (";
            StringBuilder placeholders = new StringBuilder();
            for (int i = 0; i < tickets.size(); i++) {
                placeholders.append("?");
                if (i < tickets.size() - 1) {
                    placeholders.append(", ");
                }
            }
            sql += placeholders + ")";
            PreparedStatement deleteStatement = connection.prepareStatement(sql);
            deleteStatement.setInt(1, guiId);
            deleteStatement.setInt(2, page);
            for (int i = 0; i < tickets.size(); i++) {
                deleteStatement.setInt(i + 3, tickets.get(i).slot());
            }
            deleteStatement.executeUpdate();

            //With old tickets now deleted, we can update existing ones and insert new ones too
            PreparedStatement statement = connection.prepareStatement("""
                    INSERT INTO tickets (gui_id, page, slot, tc_name, display_name, raw_display_name)
                    VALUES (?, ?, ?, ?, ?, ?)
                    ON DUPLICATE KEY UPDATE 
                        tc_name=VALUES(tc_name),
                        display_name=VALUES(display_name),
                        raw_display_name=VALUES(raw_display_name)
                    """);
            for (TicketDatabaseObject ticket : tickets) {
                statement.setInt(1, guiId);
                statement.setInt(2, page);
                statement.setInt(3, ticket.slot());
                statement.setString(4, ticket.tcName());
                statement.setString(5, ticket.colouredDisplayName());
                statement.setString(6, ticket.rawDisplayName());

                statement.addBatch();
            }
            statement.executeBatch();
        } catch (SQLException e) {
            throw new DMLException(e);
        }
    }
}
