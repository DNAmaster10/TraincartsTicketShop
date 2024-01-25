package com.dnamaster10.tcgui.objects;

import com.dnamaster10.tcgui.TraincartsGui;
import com.dnamaster10.tcgui.util.gui.GuiBuilder;
import com.dnamaster10.tcgui.util.database.LinkerAccessor;
import com.dnamaster10.tcgui.util.database.databaseobjects.LinkerDatabaseObject;
import com.dnamaster10.tcgui.util.database.databaseobjects.TicketDatabaseObject;
import com.dnamaster10.tcgui.util.database.GuiAccessor;
import com.dnamaster10.tcgui.util.database.TicketAccessor;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EditGui extends Gui {
    @Override
    public void open(Player p) {
        //Method must be run synchronous
        if (Bukkit.isPrimaryThread()) {
            p.openInventory(getInventory());
            return;
        }
        //Else, run synchronous
        Bukkit.getScheduler().runTask(TraincartsGui.getPlugin(), () -> {
            p.openInventory(getInventory());
        });
    }

    @Override
    public void nextPage(Player p) {
        Bukkit.getScheduler().runTaskAsynchronously(TraincartsGui.getPlugin(), () -> {
            //Save the current page
            save();
            //Increment the current page
            setPage(getPage() + 1);
            //Get next page
            try {
                GuiBuilder builder = new GuiBuilder(getGuiName(), getPage());
                builder.addTickets();
                builder.addPrevPageButton();
                builder.addNextPageButton();
                builder.addLinkers();
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
            //Save the current page
            save();

            //Set the new page
            setPage(getPage() - 1);
            if (getPage() < 0) {
                setPage(0);
            }

            //Create the new page
            try {
                GuiBuilder builder = new GuiBuilder(getGuiName(), getPage());
                builder.addTickets();
                if (getPage() != 0) {
                    builder.addPrevPageButton();
                }
                builder.addNextPageButton();
                builder.addLinkers();
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
    public void handleClick(InventoryClickEvent event, List<ItemStack> items) {
        //Check if clicked item is a page button
        boolean containsButton = false;
        String buttonType = null;
        for (ItemStack item : items) {
            if (!item.hasItemMeta()) {
                continue;
            }
            NamespacedKey key = new NamespacedKey(TraincartsGui.getPlugin(), "type");
            if (!Objects.requireNonNull(item.getItemMeta()).getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
                continue;
            }
            if (Objects.equals(item.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING), "button")) {
                containsButton = true;
                NamespacedKey typeKey = new NamespacedKey(TraincartsGui.getPlugin(),"button_type");
                buttonType = item.getItemMeta().getPersistentDataContainer().get(typeKey, PersistentDataType.STRING);
                break;
            }
        }
        if (!containsButton) {
            return;
        }
        switch (Objects.requireNonNull(buttonType)) {
            case "next_page" -> {
                nextPage((Player) event.getWhoClicked());
            }
            case "prev_page" -> {
                prevPage((Player) event.getWhoClicked());
            }
        }
    }

    public void save() {
        //Method must be run asynchronously
        //Saves the current gui page
        //Build an arraylist of ticket and linker database objects to be put in the database

        List<TicketDatabaseObject> ticketList = new ArrayList<>();
        NamespacedKey tcKey = new NamespacedKey(TraincartsGui.plugin, "tc_name");
        NamespacedKey priceKey = new NamespacedKey(TraincartsGui.plugin, "price");

        List<LinkerDatabaseObject> linkerList = new ArrayList<>();
        NamespacedKey guiKey = new NamespacedKey(TraincartsGui.plugin, "gui");

        for (int i = 0; i < getInventory().getSize() - 9; i++) {
            //For every possible ticket slot get the item in that slot
            ItemStack item = getInventory().getItem(i);
            if (item == null) {
                //If there isn't an item, continue to the next slot
                continue;
            }
            //Check if item is a ticket or linker
            ItemMeta meta = item.getItemMeta();
            if (meta == null) {
                continue;
            }
            if (meta.getPersistentDataContainer().has(tcKey, PersistentDataType.STRING)) {
                //Item is a ticket, handle ticket save
                String tcName = meta.getPersistentDataContainer().get(tcKey, PersistentDataType.STRING);
                String displayName = meta.getDisplayName();

                //Check if display name is too long
                if (displayName.length() > 20) {
                    continue;
                }

                //Check if price is set, if not, set price to 0
                int price = 0;
                if (meta.getPersistentDataContainer().has(priceKey, PersistentDataType.INTEGER)) {
                    price = meta.getPersistentDataContainer().get(priceKey, PersistentDataType.INTEGER);
                }

                TicketDatabaseObject ticket = new TicketDatabaseObject(i, tcName, displayName, price);
                ticketList.add(ticket);
            }
            else if (meta.getPersistentDataContainer().has(guiKey, PersistentDataType.INTEGER)) {
                //Item is a linker, handler linker save
                int linkedGuiId = meta.getPersistentDataContainer().get(guiKey, PersistentDataType.INTEGER);
                String displayName = meta.getDisplayName();

                //Check if display name is too long
                if (displayName.length() > 20) {
                    continue;
                }

                LinkerDatabaseObject linker = new LinkerDatabaseObject(i, linkedGuiId, displayName);
                linkerList.add(linker);
            }
            //Otherwise, item is not a savable / tcgui item. Ignore it to remove it
        }
        //With an array list of tickets and linkers, we can save the data to the database
        //First, delete all existing tickets and linkers for this gui and page
        try {
            TicketAccessor ticketAccessor = new TicketAccessor();
            GuiAccessor guiAccessor = new GuiAccessor();
            LinkerAccessor linkerAccessor = new LinkerAccessor();

            int guiId = guiAccessor.getGuiIdByName(getGuiName());

            ticketAccessor.deleteTicketsBYGuiIdPageId(guiId, getPage());

            //Add the tickets to the database
            ticketAccessor.addTickets(guiId, getPage(), ticketList);

            //Add the linkers to the database
            linkerAccessor.addLinkers(guiId, getPage(), linkerList);
        } catch (SQLException e) {
            TraincartsGui.plugin.reportSqlError(e.toString());
            return;
        }
    }

    public EditGui(String guiName) throws SQLException {
        //Should be called from an asynchronous thread
        setPage(0);
        setGuiName(guiName);

        //Build tickets
        GuiBuilder builder = new GuiBuilder(guiName, getPage());
        builder.addTickets();
        builder.addNextPageButton();
        builder.addLinkers();
        setInventory(builder.getInventory());
    }
}