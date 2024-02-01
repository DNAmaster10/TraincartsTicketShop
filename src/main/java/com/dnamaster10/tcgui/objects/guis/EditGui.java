package com.dnamaster10.tcgui.objects.guis;

import com.dnamaster10.tcgui.util.gui.GuiBuilder;
import com.dnamaster10.tcgui.util.database.LinkerAccessor;
import com.dnamaster10.tcgui.util.database.databaseobjects.LinkerDatabaseObject;
import com.dnamaster10.tcgui.util.database.databaseobjects.TicketDatabaseObject;
import com.dnamaster10.tcgui.util.database.GuiAccessor;
import com.dnamaster10.tcgui.util.database.TicketAccessor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EditGui extends MultipageGui {
    //Used when the next page button is clicked to decide whether to save the gui.
    //This is because the inventory close event is called when opening a new gui.
    //This value helps the gui manager to know whether a next page button was clicked, in which case it doesn't need to save
    //or whether the gui was actually closed.
    private boolean wasClosed = true;
    public boolean shouldSave() {
        getPlugin().getLogger().severe("Value: " + wasClosed);
        if (wasClosed) {
            return true;
        }
        else {
            wasClosed = true;
            return false;
        }
    }
    @Override
    public void open() {
        //Method must be run synchronous
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
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
        GuiBuilder builder = new GuiBuilder(getDisplayName());
        builder.addTicketsFromDatabase(getGuiName(), getPage());
        builder.addLinkersFromDatabase(getGuiName(), getPage());
        if (getPage() > 0) {
            builder.addPrevPageButton();
        }
        builder.addNextPageButton();
        setInventory(builder.getInventory());
    }

    //Note that we don't need to save the gui from the following methods, as the save method is called by the gui
    //manager whenever an inventory is closed. Instead, we should copy the current inventory to a temporary variable
    @Override
    public void nextPage() {
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            //Save the current gui
            save();
            wasClosed = false;

            //Increment the current page
            setPage(getPage() + 1);
            removeCursorItem();
            open();
        });
    }

    @Override
    public void prevPage() {
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            //Check there is a prev page
            if (getPage() <= 0) {
                setPage(0);
                removeCursorItemAndClose();
                return;
            }

            //Save the current gui
            save();
            wasClosed = false;

            setPage(getPage() - 1);
            removeCursorItem();
            open();
        });
    }

    @Override
    public void handleClick(InventoryClickEvent event, List<ItemStack> items) {
        //Check if clicked item is a page button
        for (ItemStack item : items) {
            String buttonType = getButtonType(item);
            if (buttonType == null) {
                continue;
            }
            switch (buttonType) {
                case "next_page" -> {
                    nextPage();
                    return;
                }
                case "prev_page" -> {
                    prevPage();
                    return;
                }
            }
        }
    }
    public void save() {
        //Saves items in inventory to the database
        List<TicketDatabaseObject> ticketList = new ArrayList<>();
        List<LinkerDatabaseObject> linkerList = new ArrayList<>();

        //Create item meta keys
        NamespacedKey buttonTypeKey = new NamespacedKey(getPlugin(), "button_type");

        //Tickets
        NamespacedKey tcNameKey = new NamespacedKey(getPlugin(), "tc_name");
        NamespacedKey priceKey = new NamespacedKey(getPlugin(), "price");

        //Linkers
        NamespacedKey guiIdKey = new NamespacedKey(getPlugin(), "gui");

        //For every item in inventory
        Inventory inventory = getInventory();
        for (int i = 0; i < inventory.getSize() - 9; i++) {
            ItemStack item = inventory.getItem(i);

            //Check if item is button
            if (item == null) {
                continue;
            }
            ItemMeta meta = item.getItemMeta();
            if (meta == null) {
                continue;
            }
            PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
            if (!dataContainer.has(buttonTypeKey, PersistentDataType.STRING)) {
                continue;
            }

            //Get button type
            String buttonType = dataContainer.get(buttonTypeKey, PersistentDataType.STRING);
            if (buttonType == null) {
                continue;
            }
            switch (buttonType) {
                case "ticket" -> {
                    //Item is a ticket. Check ticket data.
                    if (!dataContainer.has(tcNameKey, PersistentDataType.STRING)) {
                        continue;
                    }
                    if (!dataContainer.has(priceKey, PersistentDataType.INTEGER)) {
                        continue;
                    }
                    //Get data
                    String tcName = dataContainer.get(tcNameKey, PersistentDataType.STRING);
                    Integer price = dataContainer.get(priceKey, PersistentDataType.INTEGER);
                    if (price == null) {
                        continue;
                    }
                    String colouredDisplayName = meta.getDisplayName();
                    String rawDisplayName = ChatColor.stripColor(colouredDisplayName);

                    //Check display name length
                    if (colouredDisplayName.length() > 100) {
                        continue;
                    }
                    if (rawDisplayName.length() > 25) {
                        continue;
                    }

                    TicketDatabaseObject ticket = new TicketDatabaseObject(i, tcName, colouredDisplayName, rawDisplayName, price);
                    ticketList.add(ticket);
                }
                case "linker" -> {
                    //Item is a linker. Check linker data
                    if (!dataContainer.has(guiIdKey, PersistentDataType.INTEGER)) {
                        continue;
                    }
                    //Get data
                    Integer destGuiId = dataContainer.get(guiIdKey, PersistentDataType.INTEGER);
                    if (destGuiId == null) {
                        continue;
                    }
                    String colouredDisplayName = meta.getDisplayName();
                    String rawDisplayName = ChatColor.stripColor(colouredDisplayName);

                    //Check if display name is too long
                    if (colouredDisplayName.length() > 100) {
                        continue;
                    }
                    if (rawDisplayName.length() > 25) {
                        continue;
                    }

                    LinkerDatabaseObject linker = new LinkerDatabaseObject(i, destGuiId, colouredDisplayName, rawDisplayName);
                    linkerList.add(linker);
                }
                //Otherwise, item is not a savable / tcgui item. Ignore it to remove it
            }
            //Now we can save.
            //Remove existing tickets and linkers
            try {
                TicketAccessor ticketAccessor = new TicketAccessor();
                LinkerAccessor linkerAccessor = new LinkerAccessor();

                ticketAccessor.deleteTicketsByGuiIdPageId(getGuiId(), getPage());
                linkerAccessor.deleteLinkersByGuiIdPageId(getGuiId(), getPage());

                //Add items to database
                ticketAccessor.addTickets(getGuiId(), getPage(), ticketList);
                linkerAccessor.addLinkers(getGuiId(), getPage(), linkerList);
            } catch (SQLException e) {
                removeCursorItemAndClose();
                getPlugin().reportSqlError(e.toString());
            }
        }
    }

    public EditGui(String guiName, int page, Player p) throws SQLException {
        //Should be called from an asynchronous thread
        GuiAccessor guiAccessor = new GuiAccessor();
        String displayName = "Editing: " + guiAccessor.getColouredGuiDisplayName(guiName);
        int guiId = guiAccessor.getGuiIdByName(guiName);

        setGuiName(guiName);
        setDisplayName(displayName);
        setGuiId(guiId);
        setPage(page);
        setPlayer(p);
    }
    public EditGui(String guiName, Player p) throws SQLException {
        this(guiName, 0, p);
    }
}