package com.dnamaster10.tcgui.util.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TicketAccessor extends DatabaseAccessor {
    public TicketAccessor() throws SQLException {
        super();
    }
    public void deleteTicketsByGuid (int guiId) throws SQLException {
        //Returns a list of tickets registered under a given gui
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM tickets WHERE guiid=?");
            statement.setInt(1, guiId);
            statement.execute();
        }
    }
}
