package com.dnamaster10.tcgui.objects.guis;

import com.dnamaster10.tcgui.util.gui.GuiBuilder;
import com.dnamaster10.tcgui.util.database.LinkerAccessor;
import com.dnamaster10.tcgui.util.database.databaseobjects.LinkerDatabaseObject;
import com.dnamaster10.tcgui.util.database.databaseobjects.TicketDatabaseObject;
import com.dnamaster10.tcgui.util.database.GuiAccessor;
import com.dnamaster10.tcgui.util.database.TicketAccessor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

import static com.dnamaster10.tcgui.objects.buttons.DataKeys.*;

public class EditGui extends MultipageGui {
    //Used when the next page button is clicked to decide whether to save the gui.
    //This is because the inventory close event is called when opening a new gui.
    //This value helps the gui manager to know whether a next page button was clicked, in which case it doesn't need to save
    //or whether the gui was actually closed.
    private boolean wasClosed = true;
    public boolean shouldSave() {
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
                getPlugin().reportSqlError(getPlayer(), e);
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
        builder.addDeletePageButton();
        builder.addInsertPageButton();
        setInventory(builder.getInventory());
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
                case "delete_page" -> {
                    deletePage();
                    return;
                }
                case "insert_page" -> {
                    insertPage();
                    return;
                }
            }
        }
    }

    //Note that in the following methods, was Closed is set to false. This lets the gui manager know that it doesn't need
    //to save the gui to the database because it either doesn't need to save, or has already been saved.
    @Override
    protected void nextPage() {
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
    protected void prevPage() {
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
    protected void insertPage() {
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            try {
                GuiAccessor guiAccessor = new GuiAccessor();
                guiAccessor.insertPage(getGuiId(), getPage());
            } catch (SQLException e) {
                getPlugin().reportSqlError(getPlayer(), e);
            }
            //Save the current page before going to the new one
            save();
            wasClosed = false;
            //Set current page to the new page
            setPage(getPage() + 1);
            removeCursorItem();
            open();
        });
    }
    private void deletePage() {
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            int maxPage;
            try {
                GuiAccessor guiAccessor = new GuiAccessor();
                guiAccessor.deletePage(getGuiId(), getPage());
                maxPage = guiAccessor.getMaxPage(getGuiId());
            } catch (SQLException e) {
                removeCursorItemAndClose();
                getPlugin().reportSqlError(getPlayer(), e);
                return;
            }
            if (getPage() > maxPage) {
                setPage(getPage() - 1);
            }
            wasClosed = false;
            removeCursorItem();
            open();
        });
    }
    public void save() {
        //Saves items in inventory to the database
        List<TicketDatabaseObject> ticketList = new ArrayList<>();
        List<LinkerDatabaseObject> linkerList = new ArrayList<>();

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
            if (!dataContainer.has(BUTTON_TYPE, PersistentDataType.STRING)) {
                continue;
            }

            //Get button type
            String buttonType = dataContainer.get(BUTTON_TYPE, PersistentDataType.STRING);
            if (buttonType == null) {
                continue;
            }
            switch (buttonType) {
                case "ticket" -> {
                    //Item is a ticket. Check ticket data.
                    if (!dataContainer.has(TC_TICKET_NAME, PersistentDataType.STRING)) {
                        continue;
                    }
                    if (!dataContainer.has(TICKET_PRICE, PersistentDataType.INTEGER)) {
                        continue;
                    }
                    //Get data
                    String tcName = dataContainer.get(TC_TICKET_NAME, PersistentDataType.STRING);
                    Integer price = dataContainer.get(TICKET_PRICE, PersistentDataType.INTEGER);
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
                    if (!dataContainer.has(DEST_GUI_ID, PersistentDataType.INTEGER)) {
                        continue;
                    }
                    if (!dataContainer.has(DEST_GUI_PAGE, PersistentDataType.INTEGER)) {
                        continue;
                    }
                    //Get data
                    Integer destGuiId = dataContainer.get(DEST_GUI_ID, PersistentDataType.INTEGER);
                    Integer destGuiPage = dataContainer.get(DEST_GUI_PAGE, PersistentDataType.INTEGER);
                    if (destGuiId == null) {
                        continue;
                    }
                    if (destGuiPage == null) {
                        destGuiPage = 0;
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

                    LinkerDatabaseObject linker = new LinkerDatabaseObject(i, destGuiId, destGuiPage, colouredDisplayName, rawDisplayName);
                    linkerList.add(linker);
                }
                //Otherwise, item is not a savable / tcgui item. Ignore it to remove it
            }
        }
        try {
            //Save to database
            TicketAccessor ticketAccessor = new TicketAccessor();
            LinkerAccessor linkerAccessor = new LinkerAccessor();

            ticketAccessor.saveTicketPage(getGuiId(), getPage(), ticketList);
            linkerAccessor.saveLinkerPage(getGuiId(), getPage(), linkerList);
        } catch (SQLException e) {
            removeCursorItemAndClose();
            getPlugin().reportSqlError(e);
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