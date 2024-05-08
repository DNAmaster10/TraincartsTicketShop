package com.dnamaster10.traincartsticketshop.util.newdatabase.accessors;

import com.dnamaster10.traincartsticketshop.util.newdatabase.databaseobjects.TicketDatabaseObject;
import com.dnamaster10.traincartsticketshop.util.exceptions.ModificationException;
import com.dnamaster10.traincartsticketshop.util.exceptions.QueryException;
import com.dnamaster10.traincartsticketshop.util.newdatabase.DatabaseAccessorFactory;
import com.dnamaster10.traincartsticketshop.util.newdatabase.dbaccessorinterfaces.TicketsDatabaseAccessor;

import java.util.List;

public class TicketDataAccessor extends DataAccessor {
    TicketsDatabaseAccessor ticketsDatabaseAccessor = DatabaseAccessorFactory.getTicketDatabaseAccessor();

    public TicketDatabaseObject[] getTickets(int guiId, int page) throws QueryException {
        return ticketsDatabaseAccessor.getTickets(guiId, page);
    }

    public TicketDatabaseObject[] searchTickets(int guiId, int offset, String searchTerm) throws QueryException {
        return ticketsDatabaseAccessor.searchTickets(guiId, offset, searchTerm);
    }

    public int getTotalTickets(int guiId) throws QueryException {
        return ticketsDatabaseAccessor.getTotalTickets(guiId);
    }

    public int getTotalTicketSearchResults(int guiId, String searchTerm) throws QueryException {
        return ticketsDatabaseAccessor.getTotalTicketSearchResults(guiId, searchTerm);
    }

    public void saveTicketPage(int guiId, int page, List<TicketDatabaseObject> tickets) throws ModificationException {
        ticketsDatabaseAccessor.saveTicketPage(guiId, page, tickets);
    }
}