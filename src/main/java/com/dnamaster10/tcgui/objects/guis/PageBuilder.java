package com.dnamaster10.tcgui.objects.guis;

import com.dnamaster10.tcgui.objects.buttons.Button;
import com.dnamaster10.tcgui.objects.buttons.Linker;
import com.dnamaster10.tcgui.objects.buttons.SimpleHeadButton;
import com.dnamaster10.tcgui.objects.buttons.Ticket;
import com.dnamaster10.tcgui.util.database.LinkerAccessor;
import com.dnamaster10.tcgui.util.database.TicketAccessor;
import com.dnamaster10.tcgui.util.database.databaseobjects.LinkerDatabaseObject;
import com.dnamaster10.tcgui.util.database.databaseobjects.TicketDatabaseObject;

import java.sql.SQLException;

import static com.dnamaster10.tcgui.objects.buttons.HeadData.HeadType.*;

public class PageBuilder {
    private final Button[] page = new Button[54];
    public Button[] getPage() {
        return this.page;
    }
    public void addTickets(TicketDatabaseObject[] tickets) {
        for (TicketDatabaseObject ticketDatabaseObject : tickets) {
            if (ticketDatabaseObject == null) {
                continue;
            }
            //Create the button
            Ticket ticket = new Ticket(ticketDatabaseObject.tcName(), ticketDatabaseObject.colouredDisplayName());
            page[ticketDatabaseObject.slot()] = ticket;
        }
    }
    public void addLinkers(LinkerDatabaseObject[] linkers) {
        for (LinkerDatabaseObject linkerDatabaseObject : linkers) {
            if (linkerDatabaseObject == null) {
                continue;
            }
            //Create the ticket
            Linker linker = new Linker(linkerDatabaseObject.linkedGuiId(), linkerDatabaseObject.linkedGuiPage(), linkerDatabaseObject.colouredDisplayName());
            page[linkerDatabaseObject.slot()] = linker;
        }
    }
    public void addTicketsFromDatabase(int guiId, int pageNumber) throws SQLException {
        //Fetches tickets from database and adds their buttons to the gui
        TicketAccessor ticketAccessor = new TicketAccessor();
        TicketDatabaseObject[] ticketDatabaseObjects = ticketAccessor.getTickets(guiId, pageNumber);
        addTickets(ticketDatabaseObjects);
    }
    public void addLinkersFromDatabase(int guiId, int pageNumber) throws SQLException {
        //Fetches linkers from the database and adds their buttons to the gui
        LinkerAccessor linkerAccessor = new LinkerAccessor();
        LinkerDatabaseObject[] linkerDatabaseObjects = linkerAccessor.getLinkersByGuiId(guiId, pageNumber);
        addLinkers(linkerDatabaseObjects);
    }
    public void addButton(int slot, Button button) {
        page[slot] = button;
    }
    public void addNextPageButton() {
        SimpleHeadButton button = new SimpleHeadButton("next_page", CHAT_ARROW_RIGHT, "Next Page");
        addButton(53, button);
    }
    public void addPrevPageButton() {
        SimpleHeadButton button = new SimpleHeadButton("prev_page", CHAT_ARROW_LEFT, "Prev Page");
        addButton(52, button);
    }
    public void addBackButton() {
        SimpleHeadButton button = new SimpleHeadButton("back", GRAY_BACK_ARROW, "Back");
        addButton(45, button);
    }
}
