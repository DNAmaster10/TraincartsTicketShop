package com.dnamaster10.tcgui.objects.guis;

import com.dnamaster10.tcgui.util.Traincarts;
import com.dnamaster10.tcgui.util.database.GuiAccessor;
import com.dnamaster10.tcgui.util.database.TicketAccessor;
import com.dnamaster10.tcgui.util.database.databaseobjects.TicketDatabaseObject;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

import static com.dnamaster10.tcgui.TraincartsGui.getPlugin;
import static com.dnamaster10.tcgui.objects.buttons.DataKeys.TC_TICKET_NAME;

public class TicketSearchGui extends SearchGui {
    @Override
    protected void generate() throws SQLException {
        //Builds a new inventory based on current class values
        GuiBuilder builder = new GuiBuilder(getDisplayName());

        //Get tickets from database
        TicketAccessor ticketAccessor = new TicketAccessor();
        TicketDatabaseObject[] ticketArray = ticketAccessor.searchTickets(getSearchGuiId(), getPageNumber() * 45, getSearchTerm());

        //Add tickets to the inventory
        builder.addTickets(ticketArray);

        //Check whether another page is needed
        if (ticketAccessor.getTotalTicketSearchResults(getSearchGuiId(), getSearchTerm()) > (getPageNumber() + 1) * 45) {
            builder.addNextPageButton();
        }
        if (getPageNumber() > 0) {
            builder.addPrevPageButton();
        }

        setInventory(builder.getInventory());
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
        if (!Objects.requireNonNull(ticket.getItemMeta()).getPersistentDataContainer().has(TC_TICKET_NAME, PersistentDataType.STRING)) {
            removeCursorItem();
            return;
        }
        String tcName = Objects.requireNonNull(ticket.getItemMeta()).getPersistentDataContainer().get(TC_TICKET_NAME, PersistentDataType.STRING);

        //Check that the tc ticket exists
        if (!Traincarts.checkTicket(tcName)) {
            removeCursorItem();
            return;
        }
        //Give ticket to player
        Traincarts.giveTicketItem(tcName, 0, getPlayer());
        removeCursorItemAndClose();
    }

    @Override
    public void nextPage() {
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            try {
                //Check if any other pages exist beyond this one
                TicketAccessor ticketAccessor = new TicketAccessor();
                if (!(ticketAccessor.getTotalTicketSearchResults(getSearchGuiId(), getSearchTerm()) > (getPageNumber() + 1) * 45)) {
                    removeCursorItem();
                    return;
                }
            } catch (SQLException e) {
                removeCursorItemAndClose();
                getPlugin().reportSqlError(getPlayer(), e);
                return;
            }
            //Increment page
            setPageNumber(getPageNumber() + 1);

            removeCursorItem();
            open();
        });
    }

    @Override
    public void prevPage() {
        if (getPageNumber() - 1 < 0) {
            setPageNumber(0);
            removeCursorItem();
            return;
        }
        setPageNumber(getPageNumber() - 1);
        removeCursorItem();
        open();
    }

    public TicketSearchGui(String searchGuiName, String searchTerm, int page, Player p) throws SQLException {
        //Must be run async
        setSearchGuiName(searchGuiName);
        setSearchTerm(searchTerm);
        setPageNumber(page);
        setPlayer(p);

        //Get gui id
        GuiAccessor guiAccessor = new GuiAccessor();
        setSearchGuiId(guiAccessor.getGuiIdByName(searchGuiName));

        //Get gui display name
        setDisplayName("Searching: " + guiAccessor.getColouredGuiDisplayName(searchGuiName));
    }
    public TicketSearchGui(String searchGuiName, String searchTerm, Player p) throws SQLException {
        this(searchGuiName, searchTerm, 0, p);
    }
}
