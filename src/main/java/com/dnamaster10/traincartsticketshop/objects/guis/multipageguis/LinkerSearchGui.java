package com.dnamaster10.traincartsticketshop.objects.guis.multipageguis;

import com.dnamaster10.traincartsticketshop.objects.buttons.Button;
import com.dnamaster10.traincartsticketshop.objects.guis.PageBuilder;
import com.dnamaster10.traincartsticketshop.util.Utilities;
import com.dnamaster10.traincartsticketshop.util.database.mariadb.MariaDBGuiAccessor;
import com.dnamaster10.traincartsticketshop.util.database.mariadb.MariaDBLinkerAccessor;
import com.dnamaster10.traincartsticketshop.util.database.databaseobjects.LinkerDatabaseObject;
import com.dnamaster10.traincartsticketshop.util.exceptions.QueryException;
import org.bukkit.entity.Player;

public class LinkerSearchGui extends MultipagePurchasableGui {
    private final int searchGuiId;
    private final String searchTerm;
    private final int totalSearchResults;

    public LinkerSearchGui(int searchGuiId, String searchTerm, int page, Player player) throws QueryException {
        MariaDBGuiAccessor guiAccessor = new MariaDBGuiAccessor();
        MariaDBLinkerAccessor linkerAccessor = new MariaDBLinkerAccessor();

        this.searchGuiId = searchGuiId;
        this.searchTerm = searchTerm;
        this.totalSearchResults = linkerAccessor.getTotalLinkerSearchResults(searchGuiId, searchTerm);
        setPageNumber(page);
        setPlayer(player);
        setDisplayName("Searching: " + guiAccessor.getDisplayNameById(searchGuiId));
        setTotalPages(Utilities.getPageCount(totalSearchResults, 45));
    }

    @Override
    protected Button[] getNewPage() throws QueryException {
        MariaDBLinkerAccessor linkerAccessor = new MariaDBLinkerAccessor();
        PageBuilder pageBuilder = new PageBuilder();

        LinkerDatabaseObject[] linkerDatabaseObjects = linkerAccessor.searchLinkers(searchGuiId, getPageNumber() * 45, searchTerm);
        pageBuilder.addLinkers(linkerDatabaseObjects);

        if (Utilities.getPageCount(totalSearchResults, 45) > getPageNumber()) {
            pageBuilder.addNextPageButton();
        }
        if (getPageNumber() < 0) {
            pageBuilder.addPrevPageButton();
        }

        return pageBuilder.getPage();
    }
}
