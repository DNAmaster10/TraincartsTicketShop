package com.dnamaster10.traincartsticketshop.objects.guis.multipageguis;

import com.dnamaster10.traincartsticketshop.objects.buttons.Button;
import com.dnamaster10.traincartsticketshop.objects.guis.PageBuilder;
import com.dnamaster10.traincartsticketshop.util.database.GuiAccessor;
import com.dnamaster10.traincartsticketshop.util.database.LinkerAccessor;
import com.dnamaster10.traincartsticketshop.util.database.databaseobjects.LinkerDatabaseObject;
import com.dnamaster10.traincartsticketshop.util.exceptions.DQLException;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import static com.dnamaster10.traincartsticketshop.objects.buttons.Buttons.getButtonType;

public class LinkerSearchGui extends SearchGui {
    @Override
    protected Button[] generateNewPage() throws DQLException {
        PageBuilder pageBuilder = new PageBuilder();
        LinkerAccessor linkerAccessor = new LinkerAccessor();

        LinkerDatabaseObject[] linkerDatabaseObjects = linkerAccessor.searchLinkers(getSearchGuiId(), getPageNumber() * 45, getSearchTerm());
        pageBuilder.addLinkers(linkerDatabaseObjects);

        if (linkerAccessor.getTotalLinkerSearchResults(getSearchGuiId(), getSearchTerm()) > (getPageNumber() + 1) * 45) {
            pageBuilder.addNextPageButton();
        }
        if (getPageNumber() > 0) {
            pageBuilder.addPrevPageButton();
        }

        return pageBuilder.getPage();
    }

    @Override
    public void handleClick(InventoryClickEvent event, ItemStack clickedItem) {
        String buttonType = getButtonType(clickedItem);
        if (buttonType == null) {
            return;
        }
        //Remove cursor item since it is a button
        getPlayer().setItemOnCursor(null);
        switch (buttonType) {
            case "linker" -> link(clickedItem);
            case "next_page" -> nextPage();
            case "prev_page" -> prevPage();
        }
    }
    public LinkerSearchGui(int searchGuiId, String searchTerm, Player p) throws DQLException {
        //Set basic values
        setSearchGuiId(searchGuiId);
        setSearchTerm(searchTerm);
        setPageNumber(0);
        setPlayer(p);

        //Get gui display name
        GuiAccessor guiAccessor = new GuiAccessor();
        setDisplayName("Searching: " + guiAccessor.getColouredDisplayNameById(searchGuiId));
    }
}
