package com.dnamaster10.tcgui.objects;

import com.dnamaster10.tcgui.TraincartsGui;
import com.dnamaster10.tcgui.util.ButtonBuilder;
import com.dnamaster10.tcgui.util.GuiBuilder;
import com.dnamaster10.tcgui.util.TicketDatabaseObject;
import com.dnamaster10.tcgui.util.database.GuiAccessor;
import com.dnamaster10.tcgui.util.database.TicketAccessor;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EditGui extends Gui {
    @Override
    public void open(Player p) {
        //Method must be run synchronous
        if (Bukkit.isPrimaryThread()) {
            p.openInventory(getInventory());
            return;
        }
        //Else, run synchronous
        Bukkit.getScheduler().runTask(TraincartsGui.plugin, () -> {
            p.openInventory(getInventory());
        });
    }

    @Override
    public void nextPage(Player p) {

    }

    @Override
    public void prevPage(Player p) {

    }

    public void save() {
        //Method must be run asynchronously
        //Saves the current gui page
        Bukkit.getScheduler().runTaskAsynchronously(TraincartsGui.plugin, () -> {
            //Build an arraylist of Ticket database objects to be put in the database
            List<TicketDatabaseObject> ticketList = new ArrayList<>();
            NamespacedKey tcKey = new NamespacedKey(TraincartsGui.plugin, "tc_name");
            NamespacedKey priceKey = new NamespacedKey(TraincartsGui.plugin, "price");
            for (int i = 0; i < getInventory().getSize() - 1; i++) {
                //For every possible ticket slot get the item in that slot
                ItemStack item = getInventory().getItem(i);
                if (item == null) {
                    //If there isn't an item, continue to the next slot
                    continue;
                }
                //Check if item is a ticket
                ItemMeta meta = item.getItemMeta();
                if (meta == null) {
                    continue;
                }
                if (!meta.getPersistentDataContainer().has(tcKey, PersistentDataType.STRING)) {
                    continue;
                }
                int price = 0;
                //Check if price is set. If not, set it to 0
                if (meta.getPersistentDataContainer().has(priceKey, PersistentDataType.INTEGER)) {
                    price = meta.getPersistentDataContainer().get(priceKey, PersistentDataType.INTEGER);
                }
                //If it is, get data from ticket
                String tcName = meta.getPersistentDataContainer().get(tcKey, PersistentDataType.STRING);
                String displayName = meta.getDisplayName();

                TicketDatabaseObject ticket = new TicketDatabaseObject(i, tcName, displayName, price);
                ticketList.add(ticket);
            }
            //With an array list of ticket database objects, we can insert into the database
            //First delete all tickets which are currently registered
            TicketAccessor ticketAccessor;
            GuiAccessor guiAccessor;
            try {
                ticketAccessor = new TicketAccessor();
                guiAccessor = new GuiAccessor();

                int guiId = guiAccessor.getGuiIdByName(getGuiName());

                ticketAccessor.deleteTicketsBYGuiIdPageId(guiId, getPage());

                //Add the tickets to the database
                ticketAccessor.addTickets(guiId, getPage(), ticketList);
            } catch (SQLException e) {
                TraincartsGui.plugin.reportSqlError(e.toString());
                return;
            }
        });
    }

    public EditGui(String guiName) throws SQLException {
        //Should be called from an asynchronous thread
        setPage(0);
        setGuiName(guiName);

        //Build tickets
        setInventory(new GuiBuilder(guiName, getPage()).build());

        //Add UI elements
        ButtonBuilder buttonBuilder = new ButtonBuilder();
        //Add next page button (No prev page button is needed as this is first page)
        getInventory().setItem(45, buttonBuilder.getNextPageButton());
    }
}
