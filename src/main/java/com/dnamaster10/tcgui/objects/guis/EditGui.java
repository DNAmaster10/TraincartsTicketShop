package com.dnamaster10.tcgui.objects.guis;

import com.dnamaster10.tcgui.objects.buttons.*;
import com.dnamaster10.tcgui.objects.guis.confirmguis.ConfirmPageDeleteGui;
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

import static com.dnamaster10.tcgui.TraincartsGui.getPlugin;
import static com.dnamaster10.tcgui.objects.buttons.DataKeys.*;
import static com.dnamaster10.tcgui.objects.buttons.HeadData.HeadType.GREEN_PLUS;
import static com.dnamaster10.tcgui.objects.buttons.HeadData.HeadType.RED_CROSS;

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
        wasClosed = true;
        return true;
    }
    @Override
    protected void generatePage() throws SQLException {
        PageBuilder pageBuilder = new PageBuilder();

        //Add items to page
        pageBuilder.addTicketsFromDatabase(getGuiId(), getPageNumber());
        pageBuilder.addLinkersFromDatabase(getGuiId(), getPageNumber());

        //Add misc buttons
        if (getPageNumber() > 0) {
            pageBuilder.addPrevPageButton();
        }
        pageBuilder.addNextPageButton();

        SimpleButton deletePageButton = new SimpleButton("delete_page", RED_CROSS, "Delete Page");
        SimpleButton insertPageButton = new SimpleButton("insert_page", GREEN_PLUS, "Insert Page");

        pageBuilder.addSimpleButton(48, deletePageButton);
        pageBuilder.addSimpleButton(47, insertPageButton);

        //Add page to the page list
        setPage(getPageNumber(), pageBuilder.getPage());
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

    protected void insertPage() {
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            //Save the current page first
            save();
            try {
                GuiAccessor guiAccessor = new GuiAccessor();
                guiAccessor.insertPage(getGuiId(), getPageNumber());
            } catch (SQLException e) {
                getPlugin().reportSqlError(getPlayer(), e);
            }
            //Save the current page before going to the new one
            wasClosed = false;
            //Set current page to the new page
            removeCursorItem();
            open();
        });
    }
    private void deletePage() {
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            removeCursorItem();
            ConfirmPageDeleteGui newGui = new ConfirmPageDeleteGui(getGuiId(), getPageNumber(), getPlayer());
            getPlugin().getGuiManager().addGui(getPlayer(), newGui);
            wasClosed = false;
            newGui.open();
        });
    }
    public void saveToPageList() {
        //Saves the current page to the page hashmap.
        //Should be used when going between pages.
        Button[] guiButtons = new Button[54];

        //For every item in the inventory
        Inventory inventory = getInventory();
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);

            //Check if the item is a TCGui button
            if (item == null) {
                continue;
            }
            String buttonType = getButtonType(item);
            if (buttonType == null) {
                continue;
            }

            //Item is button, continue
            //TODO Here, a method may be needed and could be used across all gui classes to get a button object from a button type and an item

        }
    }
    public void saveToDatabase() {
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
                    if (tcName == null) {
                        continue;
                    }
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

            ticketAccessor.saveTicketPage(getGuiId(), getPageNumber(), ticketList);
            linkerAccessor.saveLinkerPage(getGuiId(), getPageNumber(), linkerList);
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
        setPageNumber(page);
        setPlayer(p);
    }
    public EditGui(String guiName, Player p) throws SQLException {
        this(guiName, 0, p);
    }
}