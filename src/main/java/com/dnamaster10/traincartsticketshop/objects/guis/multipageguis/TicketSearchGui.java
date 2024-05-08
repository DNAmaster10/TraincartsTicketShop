package com.dnamaster10.traincartsticketshop.objects.guis.multipageguis;

import com.dnamaster10.traincartsticketshop.objects.buttons.Button;
import com.dnamaster10.traincartsticketshop.objects.guis.PageBuilder;
import com.dnamaster10.traincartsticketshop.util.Utilities;
import com.dnamaster10.traincartsticketshop.util.database.AccessorFactory;
import com.dnamaster10.traincartsticketshop.util.database.accessorinterfaces.GuiAccessor;
import com.dnamaster10.traincartsticketshop.util.database.accessorinterfaces.TicketAccessor;
import com.dnamaster10.traincartsticketshop.util.newdatabase.databaseobjects.TicketDatabaseObject;
import com.dnamaster10.traincartsticketshop.util.exceptions.QueryException;
import org.bukkit.entity.Player;

public class TicketSearchGui extends MultipagePurchasableGui {
    private final int searchGuiId;
    private final String searchTerm;
    private final int totalSearchResults;

    public TicketSearchGui(int searchGuiId, String searchTerm, int page, Player player) throws QueryException {
        GuiAccessor guiAccessor = AccessorFactory.getGuiAccessor();
        TicketAccessor ticketAccessor = AccessorFactory.getTicketAccessor();

        this.searchGuiId = searchGuiId;
        this.searchTerm = searchTerm;
        this.totalSearchResults = ticketAccessor.getTotalTicketSearchResults(searchGuiId, searchTerm);
        setPageNumber(page);
        setPlayer(player);
        setDisplayName("Searching: " + guiAccessor.getDisplayNameById(searchGuiId));
        setTotalPages(Utilities.getPageCount(totalSearchResults, 45));
    }

    @Override
    protected Button[] getNewPage() throws QueryException {
        TicketAccessor ticketAccessor = AccessorFactory.getTicketAccessor();
        PageBuilder pageBuilder = new PageBuilder();

        TicketDatabaseObject[] ticketDatabaseObjects = ticketAccessor.searchTickets(searchGuiId, getPageNumber() * 45, searchTerm);
        pageBuilder.addTickets(ticketDatabaseObjects);

        if (Utilities.getPageCount(totalSearchResults, 45) > getPageNumber()) {
            pageBuilder.addNextPageButton();
        }
        if (getPageNumber() < 0) {
            pageBuilder.addPrevPageButton();
        }

        return pageBuilder.getPage();
    }
}
