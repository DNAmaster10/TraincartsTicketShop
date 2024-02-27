package com.dnamaster10.traincartsticketshop.objects.guis;

import com.dnamaster10.traincartsticketshop.objects.buttons.Button;
import com.dnamaster10.traincartsticketshop.objects.buttons.Linker;
import com.dnamaster10.traincartsticketshop.objects.buttons.SimpleHeadButton;
import com.dnamaster10.traincartsticketshop.objects.buttons.Ticket;
import com.dnamaster10.traincartsticketshop.objects.guis.confirmguis.ConfirmGuiDeleteGui;
import com.dnamaster10.traincartsticketshop.objects.guis.multipageguis.MultipageGui;
import com.dnamaster10.traincartsticketshop.util.database.GuiAccessor;
import com.dnamaster10.traincartsticketshop.util.database.LinkerAccessor;
import com.dnamaster10.traincartsticketshop.util.database.TicketAccessor;
import com.dnamaster10.traincartsticketshop.util.database.databaseobjects.LinkerDatabaseObject;
import com.dnamaster10.traincartsticketshop.util.database.databaseobjects.TicketDatabaseObject;
import com.dnamaster10.traincartsticketshop.util.exceptions.DMLException;
import com.dnamaster10.traincartsticketshop.util.exceptions.DQLException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.dnamaster10.traincartsticketshop.TraincartsTicketShop.getPlugin;
import static com.dnamaster10.traincartsticketshop.objects.buttons.HeadData.HeadType.GREEN_PLUS;
import static com.dnamaster10.traincartsticketshop.objects.buttons.HeadData.HeadType.RED_CROSS;
import static com.dnamaster10.traincartsticketshop.util.ButtonUtils.getButtonType;

public class EditGui extends MultipageGui {
    private boolean wasClosed;
    private final HashMap<Integer, Button[]> pages = new HashMap<>();

    public EditGui(int guiId, int page, Player player) throws DQLException {
        GuiAccessor guiAccessor = new GuiAccessor();
        setDisplayName("Editing: " + guiAccessor.getColouredDisplayNameById(guiId));
        setGuiId(guiId);
        setPageNumber(page);
        setPlayer(player);
        setTotalPages(guiAccessor.getHighestPageNumber(guiId));
    }
    public EditGui(int guiId, Player player) throws DQLException {
        this(guiId, 0, player);
    }

    public void handleCloseEvent() {
        if (wasClosed) {
            PageBuilder pageBuilder = new PageBuilder();
            pageBuilder.addInventory(getInventory());
            savePageToDatabase(pageBuilder.getPage());

            getPlugin().getGuiManager().removeEditGui(getGuiId());
            getPlayer().sendMessage(ChatColor.GREEN + "Your changes have been saved!");
            return;
        }
        wasClosed = true;
    }

    private Button[] getNewPage() throws DQLException {
        PageBuilder pageBuilder = new PageBuilder();
        pageBuilder.addTicketsFromDatabase(getGuiId(), getPageNumber());
        pageBuilder.addLinkersFromDatabase(getGuiId(), getPageNumber());

        if (getPageNumber() > 0) pageBuilder.addPrevPageButton();
        if (getPageNumber() < getTotalPages()) pageBuilder.addNextPageButton();

        SimpleHeadButton deletePageButton = new SimpleHeadButton("delete_page", RED_CROSS, "Delete Page");
        SimpleHeadButton insertPageButton = new SimpleHeadButton("insert_page", GREEN_PLUS, "Insert Page");

        pageBuilder.addButton(48, deletePageButton);
        pageBuilder.addButton(47, insertPageButton);

        return pageBuilder.getPage();
    }
    private void openPage(Button[] page) {
        String pageText = "Editing: " + getDisplayName() + "(" + (getPageNumber() + 1) + "/" + (getTotalPages() + 1);
        InventoryBuilder inventoryBuilder = new InventoryBuilder(page, pageText);
        setInventory(inventoryBuilder.getInventory());
        Bukkit.getScheduler().runTaskLater(getPlugin(), () -> getPlayer().openInventory(getInventory()), 1L);
    }
    @Override
    public void open() {
        Player editor = getPlugin().getGuiManager().getGuiEditor(getGuiId());
        if (editor != null && getPlayer().getUniqueId() != editor.getUniqueId()) {
            getPlayer().sendMessage(ChatColor.RED + "Someone else is already editing that gui");
            closeInventory();
            return;
        }
        //Proceed to open
        if (pages.containsKey(getPageNumber())) {
            openPage(pages.get(getPageNumber()));
            wasClosed = true;
            return;
        }
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            Button[] newPage;
            try {
                newPage = getNewPage();
            } catch (DQLException e) {
                openErrorGui("An error occurred generating that gui");
                getPlugin().handleSqlException(getPlayer(), e);
                return;
            }
            pages.put(getPageNumber(), newPage);
            openPage(newPage);
        });
    }

    @Override
    public void handleClick(InventoryClickEvent event, ItemStack clickedItem) {
        String buttonType = getButtonType(clickedItem);
        if (buttonType == null) return;
        switch (buttonType) {
            case "next_page", "prev_page", "delete_page", "insert_page" -> handleButtonClick(event, clickedItem, buttonType);
        }

    }
    private void handleButtonClick(InventoryClickEvent event, ItemStack clickedItem, String buttonType) {
        wasClosed = false;

        //Save the current page
        PageBuilder pageBuilder = new PageBuilder();
        pageBuilder.addInventory(event.getClickedInventory());
        Button[] page = pageBuilder.getPage();

        //Now run asynchronous to save the inventory
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            savePageToDatabase(page);
            event.getWhoClicked().setItemOnCursor(null);
            switch (buttonType) {
                case "next_page" -> {
                    pages.put(getPageNumber(), page);
                    this.nextPage();
                }
                case "prev_page" -> {
                    pages.put(getPageNumber(), page);
                    this.prevPage();
                }
                case "delete_page" -> {
                    pages.entrySet().removeIf(e -> e.getKey() >= getPageNumber());
                    this.deletePage();
                }
                case "insert_page" -> {
                    pages.entrySet().removeIf(e -> e.getKey() >= getPageNumber());
                    this.insertPage();
                }
            }
        });
    }

    @Override
    protected void nextPage() {
        if (getPageNumber() + 1 > getTotalPages()) return;
        setPageNumber(getPageNumber() + 1);
        open();
    }

    @Override
    protected void prevPage() {
        if (getPageNumber() - 1 < 0) return;
        setPageNumber(getPageNumber() - 1);
        open();
    }

    private void insertPage() {
        //TODO add check if inserting a page would be too much
        try {
            GuiAccessor guiAccessor = new GuiAccessor();
            guiAccessor.insertPage(getGuiId(), getPageNumber());
            open();
        } catch (DQLException | DMLException e) {
            getPlayer().sendMessage(ChatColor.RED + "Failed to insert page");
            closeInventory();
            getPlugin().handleSqlException(e);
        }
    }
    private void deletePage() {
        ConfirmGuiDeleteGui newGui = new ConfirmGuiDeleteGui(getGuiId(), getPlayer());
        getSession().addGui(newGui);
        newGui.open();
    }

    private void savePageToDatabase(Button[] page) {
        //TODO possible optimization to check if any changes were made before saving?
        List<TicketDatabaseObject> tickets = new ArrayList<>();
        List<LinkerDatabaseObject> linkers = new ArrayList<>();

        for (int slot = 0; slot < page.length - 9; slot++) {
            Button button = page[slot];
            if (button instanceof Ticket ticket) tickets.add(ticket.getAsDatabaseObject(slot));
            if (button instanceof Linker linker) linkers.add(linker.getAsDatabaseObject(slot));
        }
        try {
            TicketAccessor ticketAccessor = new TicketAccessor();
            LinkerAccessor linkerAccessor = new LinkerAccessor();

            ticketAccessor.saveTicketPage(getGuiId(), getPageNumber(), tickets);
            linkerAccessor.saveLinkerPage(getGuiId(), getPageNumber(), linkers);
        } catch (DQLException | DMLException e) {
            getPlayer().sendMessage(ChatColor.RED + "An error occurred saving to the database");
            closeInventory();
            getPlugin().handleSqlException(e);
        }
    }
}