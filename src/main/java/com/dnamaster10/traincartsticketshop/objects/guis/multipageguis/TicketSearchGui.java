package com.dnamaster10.traincartsticketshop.objects.guis.multipageguis;

import com.dnamaster10.traincartsticketshop.objects.buttons.Button;
import com.dnamaster10.traincartsticketshop.objects.guis.PageBuilder;
import com.dnamaster10.traincartsticketshop.util.Utilities;
import com.dnamaster10.traincartsticketshop.util.database.accessors.GuiDataAccessor;
import com.dnamaster10.traincartsticketshop.util.database.accessors.TicketDataAccessor;
import com.dnamaster10.traincartsticketshop.util.database.databaseobjects.TicketDatabaseObject;
import com.dnamaster10.traincartsticketshop.util.exceptions.QueryException;
import org.bukkit.entity.Player;

public class TicketSearchGui extends MultipagePurchasableGui {
    private final int searchGuiId;
    private final String searchTerm;
    private final int totalSearchResults;

    public TicketSearchGui(int searchGuiId, String searchTerm, int page, Player player) throws QueryException {
        GuiDataAccessor guiAccessor = new GuiDataAccessor();
        TicketDataAccessor ticketAccessor = new TicketDataAccessor();

        this.searchGuiId = searchGuiId;
        this.searchTerm = searchTerm;
        this.totalSearchResults = ticketAccessor.getTotalTicketSearchResults(searchGuiId, searchTerm);
        setPageNumber(page);
        setPlayer(player);
        setDisplayName("Searching: " + guiAccessor.getDisplayName(searchGuiId));
        setTotalPages(Utilities.getPageCount(totalSearchResults, 45));
    }

    @Override
    protected Button[] getNewPage() throws QueryException {
        TicketDataAccessor ticketAccessor = new TicketDataAccessor();
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
