package com.dnamaster10.tcgui.objects.guis;

import com.dnamaster10.tcgui.util.Traincarts;
import com.dnamaster10.tcgui.util.database.GuiAccessor;
import com.dnamaster10.tcgui.util.database.TicketAccessor;
import com.dnamaster10.tcgui.util.database.databaseobjects.TicketDatabaseObject;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.sql.SQLException;
import java.util.List;

import static com.dnamaster10.tcgui.objects.buttons.DataKeys.TC_TICKET_NAME;

public class TicketSearchGui extends SearchGui {
    @Override
    protected void generate() throws SQLException {
        PageBuilder pageBuilder = new PageBuilder();
        TicketAccessor ticketAccessor = new TicketAccessor();

        TicketDatabaseObject[] ticketDatabaseObjects = ticketAccessor.searchTickets(getSearchGuiId(), getPageNumber() * 45, getSearchTerm());
        pageBuilder.addTickets(ticketDatabaseObjects);

        if (ticketAccessor.getTotalTicketSearchResults(getSearchGuiId(), getSearchTerm()) > (getPageNumber() + 1) * 45) {
            pageBuilder.addNextPageButton();
        }
        if (getPageNumber() > 0) {
            pageBuilder.addPrevPageButton();
        }

        setInventory(new InventoryBuilder(pageBuilder.getPage(), getDisplayName()).getInventory());
    }
    @Override
    public void handleClick(InventoryClickEvent event, List<ItemStack> items) {
        for (ItemStack item : items) {
            String buttonType = getButtonType(item);
            if (buttonType == null) {
                continue;
            }
            switch (buttonType) {
                case "ticket" -> {
                    handleTicketClick(item);
                    return;
                }
                case "prev_page" -> {
                    prevPage();
                    return;
                }
                case "next_page" -> {
                    nextPage();
                    return;
                }
            }
        }
    }
    public void handleTicketClick(ItemStack ticket) {
        removeCursorItem();
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
    public TicketSearchGui(int searchGuiId, String searchTerm, int page, Player p) throws SQLException {
        //Must be run async
        setSearchGuiId(searchGuiId);
        setSearchTerm(searchTerm);
        setPageNumber(page);
        setPlayer(p);

        //Get gui display name
        GuiAccessor guiAccessor = new GuiAccessor();
        setDisplayName("Searching: " + guiAccessor.getColouredDisplayNameById(searchGuiId));
    }
}
