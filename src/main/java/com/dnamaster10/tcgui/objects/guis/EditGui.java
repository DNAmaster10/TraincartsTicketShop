package com.dnamaster10.tcgui.objects.guis;

import com.dnamaster10.tcgui.objects.buttons.*;
import com.dnamaster10.tcgui.objects.guis.confirmguis.ConfirmPageDeleteGui;
import com.dnamaster10.tcgui.util.database.LinkerAccessor;
import com.dnamaster10.tcgui.util.database.databaseobjects.LinkerDatabaseObject;
import com.dnamaster10.tcgui.util.database.databaseobjects.TicketDatabaseObject;
import com.dnamaster10.tcgui.util.database.GuiAccessor;
import com.dnamaster10.tcgui.util.database.TicketAccessor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.dnamaster10.tcgui.TraincartsGui.getPlugin;
import static com.dnamaster10.tcgui.objects.buttons.HeadData.HeadType.GREEN_PLUS;
import static com.dnamaster10.tcgui.objects.buttons.HeadData.HeadType.RED_CROSS;

public class EditGui extends MultipageGui {
    //Used when the next page button is clicked to decide whether to save the gui.
    //This is because the inventory close event is called when opening a new gui.
    //This value helps the gui manager to know whether a next page button was clicked, in which case it doesn't need to save
    //or whether the gui was actually closed.
    private boolean wasClosed = true;
    public void handleCloseEvent() {
        if (wasClosed) {
            saveCurrentPage();
            wasClosed = false;
        }
        else {
            wasClosed = true;
        }
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
        removeCursorItem();
        wasClosed = false;

        //Save the current page first
        saveCurrentPage();

        //Then, we want to delete pages from the page hashmap where they're equal to or more thn the current page, as keys will all have changed
        HashMap<Integer, Button[]> pages = getPages();
        pages.entrySet().removeIf(e -> e.getKey() >= getPageNumber());

        //Now, update items within the database
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            try {
                GuiAccessor guiAccessor = new GuiAccessor();
                guiAccessor.insertPage(getGuiId(), getPageNumber());
            } catch (SQLException e) {
                openErrorGui("An error occurred inserting that page!");
                getPlugin().reportSqlError(getPlayer(), e);
                return;
            }

            //Open the altered gui to the player
            open();
        });
    }
    private void deletePage() {
        removeCursorItem();
        wasClosed = false;
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            removeCursorItem();
            ConfirmPageDeleteGui newGui = new ConfirmPageDeleteGui(getGuiId(), getPageNumber(), getPlayer());
            getSession().addGui(newGui);
            newGui.open();
        });
    }
    public void saveCurrentPage() {
        //Saves the current page to the page hashmap and then the database
        //Should be used when going between pages or when the gui is closed.
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

            //Item is button, create a new button object from the item
            Button button = Buttons.getNewButton(buttonType, item);
            guiButtons[i] = button;
        }

        //Save the page to the gui list
        super.setPage(getPageNumber(), guiButtons);

        //Save the page to the database
        savePageToDatabase(getPageNumber(), guiButtons);
    }
    public void savePageToDatabase(int pageNumber, Button[] pageContents) {
        //Only needs to save tickets and linkers

        //Create savable lists
        List<TicketDatabaseObject> tickets = new ArrayList<>();
        List<LinkerDatabaseObject> linkers = new ArrayList<>();

        //Index counts to 9 less than total length to exclude bottom inventory row
        for (int slot = 0; slot < pageContents.length - 9; slot++) {
            //For each slot in the page
            Button button = pageContents[slot];
            if (button == null) {
                //Is not a valid button or slot is empty
                continue;
            }
            if (button instanceof Ticket ticket) {
                //If button is a ticket, save to the ticket list
                tickets.add(ticket.getAsDatabaseObject(slot));
            }
            else if (button instanceof Linker linker) {
                //if button is a linker, save to the linker list
                linkers.add(linker.getAsDatabaseObject(slot));
            }
        }

        //With lists of tickets and linkers, we can add them to the database asynchronously
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            try {
                TicketAccessor ticketAccessor = new TicketAccessor();
                LinkerAccessor linkerAccessor = new LinkerAccessor();

                ticketAccessor.saveTicketPage(getGuiId(), pageNumber, tickets);
                linkerAccessor.saveLinkerPage(getGuiId(), pageNumber, linkers);
            } catch (SQLException e) {
                removeCursorItemAndClose();
                getPlugin().reportSqlError(getPlayer(), e);
            }
        });
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