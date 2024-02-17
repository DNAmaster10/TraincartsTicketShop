package com.dnamaster10.traincartsticketshop.objects.guis.multipageguis;

import com.dnamaster10.traincartsticketshop.objects.buttons.*;
import com.dnamaster10.traincartsticketshop.objects.guis.PageBuilder;
import com.dnamaster10.traincartsticketshop.objects.guis.confirmguis.ConfirmPageDeleteGui;
import com.dnamaster10.traincartsticketshop.util.database.LinkerAccessor;
import com.dnamaster10.traincartsticketshop.util.database.databaseobjects.LinkerDatabaseObject;
import com.dnamaster10.traincartsticketshop.util.database.databaseobjects.TicketDatabaseObject;
import com.dnamaster10.traincartsticketshop.util.database.GuiAccessor;
import com.dnamaster10.traincartsticketshop.util.database.TicketAccessor;
import com.dnamaster10.traincartsticketshop.util.exceptions.DMLException;
import com.dnamaster10.traincartsticketshop.util.exceptions.DQLException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static com.dnamaster10.traincartsticketshop.TraincartsTicketShop.getPlugin;
import static com.dnamaster10.traincartsticketshop.objects.buttons.Buttons.getButtonType;
import static com.dnamaster10.traincartsticketshop.objects.buttons.HeadData.HeadType.GREEN_PLUS;
import static com.dnamaster10.traincartsticketshop.objects.buttons.HeadData.HeadType.RED_CROSS;

public class EditGui extends MultipageGui {
    private static final int pageLimit = getPlugin().getConfig().getInt("MaxPagesPerGui");
    private boolean wasClosed = true;

    public EditGui(int guiId, int page, Player p) throws DQLException {
        //Should be called from an asynchronous thread
        GuiAccessor guiAccessor = new GuiAccessor();
        String displayName = "Editing: " + guiAccessor.getColouredDisplayNameById(guiId);

        setDisplayName(displayName);
        setGuiId(guiId);
        setPageNumber(page);
        setPlayer(p);
        setMaxPages(pageLimit - 1);
    }
    public EditGui(int guiId, Player p) throws DQLException {
        this(guiId, 0, p);
    }

    public void handleCloseEvent() {
        if (wasClosed) {
            //Player has either closed the inventory, or gone to a new gui
            saveToHashmap();
            saveCurrentPageToDatabase();

            //Deregister this as an edit gui in the gui manager
            getPlugin().getGuiManager().removeEditGui(getGuiId());
            return;
        }
        wasClosed = true;
    }
    @Override
    public void open() {
        //Overriden to check if a player is already editing this gui
        //Check if there is a player editing this gui
        Player editor = getPlugin().getGuiManager().getGuiEditor(getGuiId());
        if (editor == null) {
            getPlugin().getGuiManager().addEditGui(getGuiId(), getPlayer());
            super.open();
            return;
        }
        //Someone is editing the gui, check if editor matches this guis owner
        if (getPlayer().getUniqueId() == editor.getUniqueId()) {
            super.open();
            return;
        }
        //Gui is being edited by someone else, open an error gui
        getPlayer().sendMessage(ChatColor.RED + "Someone else is already editing that gui");
        closeInventory();
    }
    @Override
    protected Button[] generateNewPage() throws DQLException {
        PageBuilder pageBuilder = new PageBuilder();

        //Add items to page
        pageBuilder.addTicketsFromDatabase(getGuiId(), getPageNumber());
        pageBuilder.addLinkersFromDatabase(getGuiId(), getPageNumber());

        //Add misc buttons
        if (getPageNumber() > 0) {
            pageBuilder.addPrevPageButton();
        }
        if (getPageNumber() < getMaxPages()) {
            pageBuilder.addNextPageButton();
        }

        SimpleHeadButton deletePageButton = new SimpleHeadButton("delete_page", RED_CROSS, "Delete Page");
        SimpleHeadButton insertPageButton = new SimpleHeadButton("insert_page", GREEN_PLUS, "Insert Page");

        pageBuilder.addButton(48, deletePageButton);
        pageBuilder.addButton(47, insertPageButton);

        //Add page to the page list
        return pageBuilder.getPage();
    }
    @Override
    public void handleClick(InventoryClickEvent event, ItemStack clickedItem) {
        //Check if clicked item is a page button
        String buttonType = getButtonType(clickedItem);
        if (buttonType == null) {
            return;
        }
        switch (buttonType) {
            case "next_page", "prev_page", "delete_page", "insert_page" -> this.handleButtonClick(event, buttonType);
        }
    }
    private void handleButtonClick(InventoryClickEvent event, String buttonType) {
        //Revert the changes before saving the page
        getInventory().setItem(event.getSlot(), event.getWhoClicked().getItemOnCursor());
        getPlayer().setItemOnCursor(null);
        switch (buttonType) {
            case "next_page" -> this.nextPage();
            case "prev_page" -> this.prevPage();
            case "delete_page" -> this.deletePage();
            case "insert_page" -> this.insertPage();
        }
    }
    //The following methods must be overriden to ensure page is saved
    private void handlePageChange() {
        wasClosed = false;
        saveToHashmap();
        saveCurrentPageToDatabase();
    }
    @Override
    protected void nextPage() {
        handlePageChange();
        super.nextPage();
    }
    @Override
    protected void prevPage() {
        handlePageChange();
        super.prevPage();
    }
    protected void insertPage() {
        wasClosed = false;

        //Now, update items within the database
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            //Get the current inventory as a page object to save to database
            PageBuilder pageBuilder = new PageBuilder();
            pageBuilder.addInventory(getInventory());
            Button[] page = pageBuilder.getPage();
            try {
                //Save the current page to the database
                //Note here that we cannot call "saveCurrentPage" method because this needs to happen before the page numbers are incremented in the database
                savePageToDatabase(getPageNumber(), page);

                //Move pages up in the database
                GuiAccessor guiAccessor = new GuiAccessor();
                guiAccessor.insertPage(getGuiId(), getPageNumber());
            } catch (DQLException | DMLException e) {
                openErrorGui("An error occurred inserting that page!");
                getPlugin().handleSqlException(getPlayer(), e);
                return;
            }
            //Remove pages more than or equal to the current page in the hashmap as these have been changed
            getPages().entrySet().removeIf(e -> e.getKey() >= getPageNumber());

            //Open the altered gui to the player
            open();
        });
    }
    private void deletePage() {
        //Clear the current gui hashmap, because we don't know what is and isn't going to be changed
        getPages().clear();
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            ConfirmPageDeleteGui newGui = new ConfirmPageDeleteGui(getGuiId(), getPageNumber(), getPlayer());
            getSession().addGui(newGui);
            newGui.open();
        });
    }
    private void saveToHashmap() {
        //Saves the current page to the page hashmap
        if (getInventory() == null) return;
        PageBuilder pageBuilder = new PageBuilder();
        pageBuilder.addInventory(getInventory());
        setPage(getPageNumber(), pageBuilder.getPage());
    }
    private void saveCurrentPageToDatabase() {
        //Can safely be called from sync thread
        if (getInventory() == null) return;
        final int currentPageNumber = getPageNumber();
        final Button[] currentPage = getPage(currentPageNumber).clone();
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> savePageToDatabase(currentPageNumber, currentPage));
    }
    private void savePageToDatabase(int pageNumber, Button[] pageContents) {
        //Saves the given page to the database
        //Only needs to save tickets and linkers - should be called from async thread

        //Create savable lists
        List<TicketDatabaseObject> tickets = new ArrayList<>();
        List<LinkerDatabaseObject> linkers = new ArrayList<>();

        //Index counts to 9 less than total length to exclude bottom inventory row
        for (int slot = 0; slot < pageContents.length - 9; slot++) {
            Button button = pageContents[slot];

            if (button instanceof Ticket ticket) {
                tickets.add(ticket.getAsDatabaseObject(slot));
            }
            else if (button instanceof Linker linker) {
                linkers.add(linker.getAsDatabaseObject(slot));
            }
        }

        //With lists of tickets and linkers, we can add them to the database asynchronously
        try {
            TicketAccessor ticketAccessor = new TicketAccessor();
            LinkerAccessor linkerAccessor = new LinkerAccessor();

            ticketAccessor.saveTicketPage(getGuiId(), pageNumber, tickets);
            linkerAccessor.saveLinkerPage(getGuiId(), pageNumber, linkers);
        } catch (DQLException | DMLException e) {
            getPlugin().handleSqlException(getPlayer(), e);
        }
    }
}