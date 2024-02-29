package com.dnamaster10.traincartsticketshop.objects.guis.multipageguis;

import com.dnamaster10.traincartsticketshop.objects.buttons.Button;
import com.dnamaster10.traincartsticketshop.objects.guis.PageBuilder;
import com.dnamaster10.traincartsticketshop.util.Utilities;
import com.dnamaster10.traincartsticketshop.util.database.AccessorFactory;
import com.dnamaster10.traincartsticketshop.util.database.accessorinterfaces.GuiAccessor;
import com.dnamaster10.traincartsticketshop.util.database.accessorinterfaces.LinkAccessor;
import com.dnamaster10.traincartsticketshop.util.database.databaseobjects.LinkDatabaseObject;
import com.dnamaster10.traincartsticketshop.util.exceptions.QueryException;
import org.bukkit.entity.Player;

public class LinkSearchGui extends MultipagePurchasableGui {
    private final int searchGuiId;
    private final String searchTerm;
    private final int totalSearchResults;

    public LinkSearchGui(int searchGuiId, String searchTerm, int page, Player player) throws QueryException {
        GuiAccessor guiAccessor = AccessorFactory.getGuiAccessor();
        LinkAccessor linkAccessor = AccessorFactory.getLinkAccessor();

        this.searchGuiId = searchGuiId;
        this.searchTerm = searchTerm;
        this.totalSearchResults = linkAccessor.getTotalLinkSearchResults(searchGuiId, searchTerm);
        setPageNumber(page);
        setPlayer(player);
        setDisplayName("Searching: " + guiAccessor.getDisplayNameById(searchGuiId));
        setTotalPages(Utilities.getPageCount(totalSearchResults, 45));
    }

    @Override
    protected Button[] getNewPage() throws QueryException {
        LinkAccessor linkAccessor = AccessorFactory.getLinkAccessor();
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
