package com.dnamaster10.tcgui.objects;

import com.dnamaster10.tcgui.util.Traincarts;
import com.dnamaster10.tcgui.util.database.GuiAccessor;
import com.dnamaster10.tcgui.util.database.TicketAccessor;
import com.dnamaster10.tcgui.util.database.databaseobjects.TicketDatabaseObject;
import com.dnamaster10.tcgui.util.gui.GuiBuilder;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class SearchGui extends Gui {
    String searchTerm;
    @Override
    public void open() {
        //Opens the gui to the player
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            //Generate new gui
            try {
                generate();
            } catch (SQLException e) {
                removeCursorItemAndClose();
                getPlugin().reportSqlError(getPlayer(), e.toString());
            }
            Bukkit.getScheduler().runTask(getPlugin(), () -> getPlayer().openInventory(getInventory()));
        });
    }
    @Override
    protected void generate() throws SQLException {
        //Builds a new inventory based on current class values
        GuiBuilder builder = new GuiBuilder(getGuiName(), getDisplayName());

        //Get tickets from database
        TicketAccessor ticketAccessor = new TicketAccessor();
        TicketDatabaseObject[] ticketArray = ticketAccessor.searchTickets(getGuiId(), getPage() * 45, this.searchTerm);

        //Add tickets to the inventory
        builder.addTickets(ticketArray);

        //Check whether another page is needed
        if (ticketAccessor.getTotalTicketSearchResults(getGuiId(), searchTerm) > (getPage() + 1) * 45) {
            builder.addNextPageButton();
        }
        if (getPage() > 0) {
            builder.addPrevPageButton();
        }

        setInventory(builder.getInventory());
    }

    @Override
    public void nextPage() {
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            try {
                //Check if any other pages exist beyond this one
                TicketAccessor ticketAccessor = new TicketAccessor();
                if (!(ticketAccessor.getTotalTicketSearchResults(getGuiId(), searchTerm) > (getPage() + 1) * 45)) {
                    removeCursorItem();
                    return;
                }
            } catch (SQLException e) {
                removeCursorItemAndClose();
                getPlugin().reportSqlError(getPlayer(), e.toString());
                return;
            }
            //Increment page
            setPage(getPage() + 1);

            removeCursorItem();
            open();
        });
    }

    @Override
    public void prevPage() {
        if (getPage() - 1 < 0) {
            setPage(0);
            removeCursorItem();
            return;
        }
        setPage(getPage() - 1);
        removeCursorItem();
        open();
    }

    public void handleTicketClick(ItemStack ticket) {
        NamespacedKey key = new NamespacedKey(getPlugin(), "tc_name");
        if (!Objects.requireNonNull(ticket.getItemMeta()).getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
            removeCursorItem();
            return;
        }
        String tcName = Objects.requireNonNull(ticket.getItemMeta()).getPersistentDataContainer().get(key, PersistentDataType.STRING);

        //Check that the tc ticket exists
        if (!Traincarts.checkTicket(tcName)) {
            removeCursorItem();
            return;
        }
        //Give ticket to player
        Traincarts.giveTicketItem(tcName, 0, getPlayer());
        removeCursorItem();
        getPlayer().closeInventory();
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
    public SearchGui(String guiName, String searchTerm, int page, Player p) throws SQLException {
        //Must be run async
        setGuiName(guiName);
        this.searchTerm = searchTerm;
        setPlayer(p);
        setPage(page);

        //Get gui id
        GuiAccessor guiAccessor = new GuiAccessor();
        setGuiId(guiAccessor.getGuiIdByName(guiName));

        //Get gui display name
        setDisplayName(guiAccessor.getColouredGuiDisplayName(guiName));
    }
    public SearchGui(String guiName, String searchTerm, Player p) throws SQLException {
        this(guiName, searchTerm, 0, p);
    }
}
