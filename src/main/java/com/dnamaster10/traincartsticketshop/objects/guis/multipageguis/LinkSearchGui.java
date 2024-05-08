package com.dnamaster10.traincartsticketshop.objects.guis.multipageguis;

import com.dnamaster10.traincartsticketshop.objects.buttons.Button;
import com.dnamaster10.traincartsticketshop.objects.guis.PageBuilder;
import com.dnamaster10.traincartsticketshop.util.Utilities;
import com.dnamaster10.traincartsticketshop.util.newdatabase.accessors.GuiDataAccessor;
import com.dnamaster10.traincartsticketshop.util.newdatabase.accessors.LinkDataAccessor;
import com.dnamaster10.traincartsticketshop.util.newdatabase.databaseobjects.LinkDatabaseObject;
import com.dnamaster10.traincartsticketshop.util.exceptions.QueryException;
import org.bukkit.entity.Player;

public class LinkSearchGui extends MultipagePurchasableGui {
    private final int searchGuiId;
    private final String searchTerm;
    private final int totalSearchResults;

    public LinkSearchGui(int searchGuiId, String searchTerm, int page, Player player) throws QueryException {
        GuiDataAccessor guiAccessor = new GuiDataAccessor();
        LinkDataAccessor linkAccessor = new LinkDataAccessor();

        this.searchGuiId = searchGuiId;
        this.searchTerm = searchTerm;
        this.totalSearchResults = linkAccessor.getTotalLinkSearchResults(searchGuiId, searchTerm);
        setPageNumber(page);
        setPlayer(player);
        setDisplayName("Searching: " + guiAccessor.getDisplayName(searchGuiId));
        setTotalPages(Utilities.getPageCount(totalSearchResults, 45));
    }

    @Override
    protected Button[] getNewPage() throws QueryException {
        LinkDataAccessor linkAccessor = new LinkDataAccessor();
        PageBuilder pageBuilder = new PageBuilder();

        LinkDatabaseObject[] linkDatabaseObjects = linkAccessor.searchLinks(searchGuiId, getPageNumber() * 45, searchTerm);
        pageBuilder.addLinks(linkDatabaseObjects);

        if (Utilities.getPageCount(totalSearchResults, 45) > getPageNumber()) {
            pageBuilder.addNextPageButton();
        }
        if (getPageNumber() < 0) {
            pageBuilder.addPrevPageButton();
        }

        return pageBuilder.getPage();
    }
}
