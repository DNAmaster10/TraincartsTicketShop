package com.dnamaster10.tcgui.objects.guis.multipageguis;

import com.dnamaster10.tcgui.objects.buttons.*;
import com.dnamaster10.tcgui.objects.guis.PageBuilder;
import com.dnamaster10.tcgui.objects.guis.confirmguis.ConfirmPageDeleteGui;
import com.dnamaster10.tcgui.util.database.LinkerAccessor;
import com.dnamaster10.tcgui.util.database.databaseobjects.LinkerDatabaseObject;
import com.dnamaster10.tcgui.util.database.databaseobjects.TicketDatabaseObject;
import com.dnamaster10.tcgui.util.database.GuiAccessor;
import com.dnamaster10.tcgui.util.database.TicketAccessor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.dnamaster10.tcgui.TraincartsGui.getPlugin;
import static com.dnamaster10.tcgui.objects.buttons.Buttons.getButtonType;
import static com.dnamaster10.tcgui.objects.buttons.HeadData.HeadType.GREEN_PLUS;
import static com.dnamaster10.tcgui.objects.buttons.HeadData.HeadType.RED_CROSS;

public class EditGui extends MultipageGui {
    //Used when the next page button is clicked to decide whether to save the gui.
    //This is because the inventory close event is called when opening a new gui.
    //This value helps the gui manager to know whether a next page button was clicked, in which case it doesn't need to save
    //or whether the gui was actually closed.
    private static final int pageLimit = getPlugin().getConfig().getInt("MaxPagesPerGui");
    private boolean wasClosed = true;
    public void handleCloseEvent() {
        if (wasClosed) {
            saveToHashmap();
            savePageToDatabase(getPageNumber(), getPage(getPageNumber()));
            wasClosed = false;
        }
        else {
            wasClosed = true;
        }
    }
    @Override
    protected Button[] generateNewPage() throws SQLException {
        PageBuilder pageBuilder = new PageBuilder();

        //Add items to page
        pageBuilder.addTicketsFromDatabase(getGuiId(), getPageNumber());
        pageBuilder.addLinkersFromDatabase(getGuiId(), getPageNumber());

        //Add misc buttons
        if (getPageNumber() > 0) {
            pageBuilder.addPrevPageButton();
        }
        pageBuilder.addNextPageButton();

        SimpleHeadButton deletePageButton = new SimpleHeadButton("delete_page", RED_CROSS, "Delete Page");
        SimpleHeadButton insertPageButton = new SimpleHeadButton("insert_page", GREEN_PLUS, "Insert Page");

        pageBuilder.addButton(48, deletePageButton);
        pageBuilder.addButton(47, insertPageButton);

        //Add page to the page list
        return pageBuilder.getPage();
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
    //The following methods must be overriden to ensure page is saved
    @Override
    protected void nextPage() {
        saveToHashmap();
        savePageToDatabase(getPageNumber(), getPage(getPageNumber()));
        super.nextPage();
    }
    @Override
    protected void prevPage() {
        saveToHashmap();
        savePageToDatabase(getPageNumber(), getPage(getPageNumber()));
        super.prevPage();
    }
    protected void insertPage() {
        removeCursorItem();
        wasClosed = false;

        //Now, update items within the database
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            //Get the current inventory as a page object to save to database
            PageBuilder pageBuilder = new PageBuilder();
            pageBuilder.addInventory(getInventory());
            Button[] page = pageBuilder.getPage();
            try {
                //Save the current page to the database
                savePageToDatabase(getPageNumber(), page);

                //Move pages up in the database
                GuiAccessor guiAccessor = new GuiAccessor();
                guiAccessor.insertPage(getGuiId(), getPageNumber());
            } catch (SQLException e) {
                openErrorGui("An error occurred inserting that page!");
                getPlugin().reportSqlError(getPlayer(), e);
                return;
            }
            //Remove pages more than or equal to the current page in the hashmap as these have been changed
            getPages().entrySet().removeIf(e -> e.getKey() >= getPageNumber());

            //Open the altered gui to the player
            open();
        });
    }
    private void deletePage() {
        removeCursorItem();
        wasClosed = false;

        //Clear the current gui hashmap, because we don't know what is and isn't going to be changed
        getPages().clear();
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            removeCursorItem();
            ConfirmPageDeleteGui newGui = new ConfirmPageDeleteGui(getGuiId(), getPageNumber(), getPlayer());
            getSession().addGui(newGui);
            newGui.open();
        });
    }
    public void saveToHashmap() {
        //Saves the current page to the page hashmap
        PageBuilder pageBuilder = new PageBuilder();
        pageBuilder.addInventory(getInventory());
        setPage(getPageNumber(), pageBuilder.getPage());
    }
    public void savePageToDatabase(int pageNumber, Button[] pageContents) {
        //Saves the given page to the database
        //Only needs to save tickets and linkers - should be called from async thread

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
        try {
            TicketAccessor ticketAccessor = new TicketAccessor();
            LinkerAccessor linkerAccessor = new LinkerAccessor();

            ticketAccessor.saveTicketPage(getGuiId(), pageNumber, tickets);
            linkerAccessor.saveLinkerPage(getGuiId(), pageNumber, linkers);
        } catch (SQLException e) {
            removeCursorItemAndClose();
            getPlugin().reportSqlError(getPlayer(), e);
        }
    }

    public EditGui(int guiId, int page, Player p) throws SQLException {
        //Should be called from an asynchronous thread
        GuiAccessor guiAccessor = new GuiAccessor();
        String displayName = "Editing: " + guiAccessor.getColouredDisplayNameById(guiId);

        setDisplayName(displayName);
        setGuiId(guiId);
        setPageNumber(page);
        setPlayer(p);
        setMaxPage(pageLimit);
    }
    public EditGui(int guiId, Player p) throws SQLException {
        this(guiId, 0, p);
    }
}