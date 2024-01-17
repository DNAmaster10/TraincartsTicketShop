package com.dnamaster10.tcgui.objects;

import com.dnamaster10.tcgui.TraincartsGui;
import com.dnamaster10.tcgui.util.GuiBuilder;
import com.dnamaster10.tcgui.util.Traincarts;
import com.dnamaster10.tcgui.util.database.GuiAccessor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

import static com.bergerkiller.bukkit.tc.tickets.TicketStore.getTicket;

public class ShopGui extends Gui {
    @Override
    public void open(Player p) {
        //Method should be run synchronous
        if (Bukkit.isPrimaryThread()) {
            p.openInventory(getInventory());
            return;
        }
        Bukkit.getScheduler().runTask(TraincartsGui.getPlugin(), () -> {
            p.openInventory(getInventory());
        });
    }

    @Override
    public void nextPage(Player p) {
        Bukkit.getScheduler().runTaskAsynchronously(TraincartsGui.getPlugin(), () -> {
            try {
                //Check if any other pages exist above this one
                GuiAccessor guiAccessor = new GuiAccessor();
                int guiId = guiAccessor.getGuiIdByName(getGuiName());
                int maxPage = guiAccessor.getTotalPages(guiId);
                if (getPage() + 1 > maxPage) {
                    return;
                }
                //Increment page
                setPage(getPage() + 1);

                //Build new inventory
                GuiBuilder builder = new GuiBuilder(getGuiName(), getPage());
                builder.addTickets();
                if (!(getPage() + 1 > maxPage)) {
                    builder.addNextPageButton();
                }
                builder.addPrevPageButton();
                updateNewInventory(builder.getInventory());
                Bukkit.getScheduler().runTaskLater(TraincartsGui.getPlugin(), () -> {
                    p.setItemOnCursor(null);
                    p.updateInventory();
                }, 1L);
            } catch (SQLException e) {
                p.closeInventory();
                TraincartsGui.getPlugin().reportSqlError(p, e.toString());
            }
        });
    }

    @Override
    public void prevPage(Player p) {
        Bukkit.getScheduler().runTaskAsynchronously(TraincartsGui.getPlugin(), () -> {
            try {
                //Check that any pages exist before the current page
                if (getPage() - 1 < 0) {
                    setPage(0);
                    return;
                }
                setPage(getPage() - 1);

                //Build the new page
                GuiBuilder builder = new GuiBuilder(getGuiName(), getPage());
                builder.addTickets();
                if (getPage() != 0) {
                    builder.addPrevPageButton();
                }
                builder.addNextPageButton();
                updateNewInventory(builder.getInventory());
                Bukkit.getScheduler().runTaskLater(TraincartsGui.getPlugin(), () -> {
                    p.setItemOnCursor(null);
                    p.updateInventory();
                }, 1L);
            } catch (SQLException e) {
                p.closeInventory();
                TraincartsGui.getPlugin().reportSqlError(p, e.toString());
            }
        });
    }
    private void handleButtonClick(InventoryClickEvent event, String buttonType) {
        switch (buttonType) {
            case "next_page" -> {
                nextPage((Player) event.getWhoClicked());
            }
            case "prev_page" -> {
                prevPage((Player) event.getWhoClicked());
            }
        }
    }
    private void handleTicketClick(InventoryClickEvent event, ItemStack ticket) {
        //Get ticket tc name
        NamespacedKey key = new NamespacedKey(TraincartsGui.getPlugin(), "tc_name");
        String tcName = Objects.requireNonNull(ticket.getItemMeta()).getPersistentDataContainer().get(key, PersistentDataType.STRING);

        //Check that the tc ticket exists
        if (!Traincarts.checkTicket(tcName)) {
            return;
        }
        //Give the ticket to the player
        Player p = (Player) event.getWhoClicked();
        Traincarts.giveTicketItem(tcName, 0, p);
        p.closeInventory();
        p.sendMessage(ChatColor.GREEN + "You purchased a ticket! Look!");
    }

    @Override
    public void handleClick(InventoryClickEvent event, List<ItemStack> items) {
        //Check if player interacted with a button
        for (ItemStack item : items) {
            if (!item.hasItemMeta()) {
                continue;
            }
            NamespacedKey key = new NamespacedKey(TraincartsGui.getPlugin(), "type");
            if (!Objects.requireNonNull(item.getItemMeta()).getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
                continue;
            }
            if (Objects.equals(item.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING), "button")) {
                //This is a button, handle button click
                NamespacedKey buttonKey = new NamespacedKey(TraincartsGui.getPlugin(), "button_type");
                String buttonType = item.getItemMeta().getPersistentDataContainer().get(buttonKey, PersistentDataType.STRING);
                handleButtonClick(event, buttonType);
                break;
            }
            else if (Objects.equals(item.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING), "ticket")) {
                //This is a ticket, handle ticket click
                handleTicketClick(event, item);
                break;
            }
        }
    }

    public ShopGui(String guiName) throws SQLException {
        //Should be called from async thread
        setPage(0);
        setGuiName(guiName);

        //Build tickets
        GuiBuilder builder = new GuiBuilder(guiName, getPage());
        builder.addTickets();

        //Check if there are any more pages
        GuiAccessor accessor = new GuiAccessor();
        int guiId = accessor.getGuiIdByName(guiName);
        if (accessor.getTotalPages(guiId) > getPage()) {
            builder.addNextPageButton();
        }
        setInventory(builder.getInventory());
    }
}
