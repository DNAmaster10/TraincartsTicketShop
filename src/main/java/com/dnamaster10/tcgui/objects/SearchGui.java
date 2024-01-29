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
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class SearchGui extends Gui {
    String searchTerm;
    int guiId;
    @Override
    public void open() {
        //Opens the gui to the player
        if (Bukkit.isPrimaryThread()) {
            getPlayer().openInventory(getInventory());
            return;
        }
        Bukkit.getScheduler().runTask(getPlugin(), () -> {
            getPlayer().openInventory(getInventory());
        });
    }

    @Override
    public void nextPage() {
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            try {
                //Check if any other pages exist beyond this one
                TicketAccessor ticketAccessor = new TicketAccessor();
                if (!(ticketAccessor.getTotalTicketSearchResults(guiId, searchTerm) > (getPage() + 1) * 45)) {
                    removeCursorItem();
                    return;
                }
                //Increment page
                setPage(getPage() + 1);

                //Build new inventory
                generateGui();
                removeCursorItem();
            } catch (SQLException e) {
                getPlugin().reportSqlError(getPlayer(), e.toString());
            }
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
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            try {
                generateGui();
                removeCursorItem();
            } catch (SQLException e) {
                getPlayer().closeInventory();
                getPlugin().reportSqlError(getPlayer(), e.toString());
            }
        });
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
    public void generateGui() throws SQLException {
        //Should be run async
        //Build tickets
        TicketAccessor ticketAccessor = new TicketAccessor();

        //Init gui builder
        GuiBuilder guiBuilder = new GuiBuilder(getGuiName());

        //Get tickets from database
        TicketDatabaseObject[] ticketArray = ticketAccessor.searchTickets(guiId, getPage() * 45 , this.searchTerm);

        //Add these tickets to the inventory
        guiBuilder.addTickets(ticketArray);

        //Check whether another page is needed
        if (ticketAccessor.getTotalTicketSearchResults(guiId, searchTerm) > (getPage() + 1) * 45) {
            guiBuilder.addNextPageButton();
        }
        if (getPage() > 0) {
            guiBuilder.addPrevPageButton();
        }

        updateNewInventory(guiBuilder.getInventory());
    }
    public SearchGui(String guiName, String searchTerm, Player p) throws SQLException {
        setGuiName(guiName);
        this.searchTerm = searchTerm;
        setPlayer(p);
        setPage(0);
        GuiAccessor guiAccessor = new GuiAccessor();
        this.guiId = guiAccessor.getGuiIdByName(guiName);

        GuiBuilder builder = new GuiBuilder(guiName);
        setInventory(builder.getInventory());
        generateGui();
    }
}
