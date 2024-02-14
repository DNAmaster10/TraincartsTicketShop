package com.dnamaster10.traincartsticketshop.objects.guis.multipageguis;

import com.dnamaster10.traincartsticketshop.objects.buttons.Button;
import com.dnamaster10.traincartsticketshop.objects.buttons.SimpleItemButton;
import com.dnamaster10.traincartsticketshop.objects.guis.PageBuilder;
import com.dnamaster10.traincartsticketshop.objects.guis.SearchSelectGui;
import com.dnamaster10.traincartsticketshop.util.Traincarts;
import com.dnamaster10.traincartsticketshop.util.database.GuiAccessor;
import com.dnamaster10.traincartsticketshop.util.exceptions.DQLException;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.sql.SQLException;

import static com.dnamaster10.traincartsticketshop.objects.buttons.Buttons.getButtonType;
import static com.dnamaster10.traincartsticketshop.objects.buttons.DataKeys.*;

public class ShopGui extends MultipageGui {
    @Override
    protected Button[] generateNewPage() throws DQLException {
        GuiAccessor guiAccessor = new GuiAccessor();

        //Build tickets and linkers
        PageBuilder pageBuilder = new PageBuilder();
        pageBuilder.addTicketsFromDatabase(getGuiId(), getPageNumber());
        pageBuilder.addLinkersFromDatabase(getGuiId(), getPageNumber());


        //Check if there are any more pages
        if (getMaxPage() > getPageNumber()) {
            pageBuilder.addNextPageButton();
        }
        if (getPageNumber() > 0) {
            pageBuilder.addPrevPageButton();
        }

        //Check if back button is needed
        if (getSession().checkBack()) {
            pageBuilder.addBackButton();
        }

        SimpleItemButton searchButton = new SimpleItemButton("search", Material.SPYGLASS, "Search This Gui");
        pageBuilder.addButton(49, searchButton);

        //Add the page to the pages hashmap
        return pageBuilder.getPage();
    }
    @Override
    public void handleClick(InventoryClickEvent event, ItemStack clickedItem) {
        //Check if player interacted with a button
        String buttonType = getButtonType(clickedItem);
        if (buttonType == null) {
            return;
        }
        //Remove the item from cursor since it is a button
        getPlayer().setItemOnCursor(null);
        switch (buttonType) {
            case "ticket" -> handleTicketClick(clickedItem);
            case "prev_page" -> prevPage();
            case "next_page" -> nextPage();
            case "linker" -> link(clickedItem);
            case "back" -> back();
            case "search" -> search();
        }
    }
    private void search() {
        //Get the gui name
        SearchSelectGui gui = new SearchSelectGui(getGuiId(), getPlayer());
        getSession().addGui(gui);
        gui.open();
    }
    private void handleTicketClick(ItemStack ticket) {
        //Get ticket data
        ItemMeta meta = ticket.getItemMeta();
        if (meta == null) {
            return;
        }
        PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
        if (!dataContainer.has(TC_TICKET_NAME, PersistentDataType.STRING)) {
            return;
        }
        String tcName = dataContainer.get(TC_TICKET_NAME, PersistentDataType.STRING);

        //Check that the tc ticket exists
        if (!Traincarts.checkTicket(tcName)) {
            return;
        }

        //Give ticket to player
        Traincarts.giveTicketItem(tcName, getPlayer());
        removeCursorItemAndClose();
    }
    public ShopGui(int guiId, int page, Player p) throws DQLException {
        //Should be called from async thread
        GuiAccessor guiAccessor = new GuiAccessor();
        String displayName = guiAccessor.getColouredDisplayNameById(guiId);

        setDisplayName(displayName);
        setPlayer(p);
        setPageNumber(page);
        setGuiId(guiId);
        setMaxPage(guiAccessor.getMaxPage(guiId));
    }
    public ShopGui(int guiId, Player p) throws DQLException {
        this(guiId, 0, p);
    }
}
