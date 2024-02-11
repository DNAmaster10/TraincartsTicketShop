package com.dnamaster10.tcgui.objects.guis;

import com.dnamaster10.tcgui.objects.buttons.Button;
import com.dnamaster10.tcgui.objects.buttons.Linker;
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
            Ticket ticket = new Ticket(ticketDatabaseObject.getTcName(), ticketDatabaseObject.getColouredDisplayName());
            page[ticketDatabaseObject.getSlot()] = ticket;
        }
    }
    public void addLinkers(LinkerDatabaseObject[] linkers) {
        for (LinkerDatabaseObject linkerDatabaseObject : linkers) {
            if (linkerDatabaseObject == null) {
                continue;
            }
            //Create the ticket
            Linker linker = new Linker(linkerDatabaseObject.getLinkedGuiId(), linkerDatabaseObject.getLinkedGuiPage(), linkerDatabaseObject.getColouredDisplayName());
            page[linkerDatabaseObject.getSlot()] = linker;
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
    public void addSimpleButton(int slot, SimpleButton button) {
        page[slot] = button;
    }
    public void addNextPageButton() {
        SimpleButton button = new SimpleButton("next_page", CHAT_ARROW_RIGHT, "Next Page");
        addSimpleButton(53, button);
    }
    public void addPrevPageButton() {
        SimpleButton button = new SimpleButton("prev_page", CHAT_ARROW_LEFT, "Prev Page");
        addSimpleButton(52, button);
    }
    public void addBackButton() {
        SimpleButton button = new SimpleButton("back", GRAY_BACK_ARROW, "Back");
        addSimpleButton(45, button);
    }
}
