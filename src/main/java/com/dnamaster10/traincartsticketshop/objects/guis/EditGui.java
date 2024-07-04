package com.dnamaster10.traincartsticketshop.objects.guis;

import com.dnamaster10.traincartsticketshop.objects.buttons.Button;
import com.dnamaster10.traincartsticketshop.objects.buttons.Link;
import com.dnamaster10.traincartsticketshop.objects.buttons.SimpleHeadButton;
import com.dnamaster10.traincartsticketshop.objects.buttons.Ticket;
import com.dnamaster10.traincartsticketshop.objects.guis.confirmguis.ConfirmPageDeleteGui;
import com.dnamaster10.traincartsticketshop.objects.guis.multipageguis.MultipageGui;
import com.dnamaster10.traincartsticketshop.util.ButtonUtils;
import com.dnamaster10.traincartsticketshop.util.Traincarts;
import com.dnamaster10.traincartsticketshop.util.database.accessors.GuiDataAccessor;
import com.dnamaster10.traincartsticketshop.util.database.accessors.LinkDataAccessor;
import com.dnamaster10.traincartsticketshop.util.database.accessors.TicketDataAccessor;
import com.dnamaster10.traincartsticketshop.util.database.databaseobjects.LinkDatabaseObject;
import com.dnamaster10.traincartsticketshop.util.database.databaseobjects.TicketDatabaseObject;
import com.dnamaster10.traincartsticketshop.util.exceptions.ModificationException;
import com.dnamaster10.traincartsticketshop.util.exceptions.QueryException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

import static com.dnamaster10.traincartsticketshop.TraincartsTicketShop.getPlugin;
import static com.dnamaster10.traincartsticketshop.objects.buttons.HeadData.HeadType.GREEN_PLUS;
import static com.dnamaster10.traincartsticketshop.objects.buttons.HeadData.HeadType.RED_CROSS;
import static com.dnamaster10.traincartsticketshop.util.ButtonUtils.getButtonType;

public class EditGui extends MultipageGui {
    private boolean wasClosed;
    private final HashMap<Integer, Button[]> pages = new HashMap<>();

    public EditGui(int guiId, int page, Player player) throws QueryException {
        GuiDataAccessor guiAccessor = new GuiDataAccessor();
        setDisplayName("Editing: " + guiAccessor.getDisplayName(guiId));
        setGuiId(guiId);
        setPageNumber(page);
        setPlayer(player);
        setTotalPages(guiAccessor.getHighestPageNumber(guiId));
        if (getTotalPages() <= getPageNumber() + 1) setTotalPages(getPageNumber() + 1);
    }
    public EditGui(int guiId, Player player) throws QueryException {
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

    private Button[] getNewPage() throws QueryException {
        PageBuilder pageBuilder = new PageBuilder();
        pageBuilder.addTicketsFromDatabase(getGuiId(), getPageNumber());
        pageBuilder.addLinksFromDatabase(getGuiId(), getPageNumber());

        if (getPageNumber() > 0) pageBuilder.addPrevPageButton();
        if (getPageNumber() + 1 < getMaxPages()) pageBuilder.addNextPageButton();

        if (getTotalPages() < getMaxPages()) {
            SimpleHeadButton insertPageButton = new SimpleHeadButton("insert_page", GREEN_PLUS, "Insert Page");
            pageBuilder.addButton(47, insertPageButton);
        }

        SimpleHeadButton deletePageButton = new SimpleHeadButton("delete_page", RED_CROSS, "Delete Page");
        pageBuilder.addButton(48, deletePageButton);

        return pageBuilder.getPage();
    }
    private void openPage(Button[] page) {
        int totalPageNum = getTotalPages();
        if (totalPageNum > getMaxPages()) totalPageNum = getMaxPages();
        String pageText = "Editing: " + getDisplayName() + "(" + (getPageNumber() + 1) + "/" + totalPageNum + ")";
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
        } else if (editor == null) {
            //Register the editor
            getPlugin().getGuiManager().addEditGui(getGuiId(), getPlayer());
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
            } catch (QueryException e) {
                openErrorGui("An error occurred generating that gui");
                getPlugin().handleSqlException(getPlayer(), e);
                return;
            }
            pages.put(getPageNumber(), newPage);
            openPage(newPage);
            wasClosed = true;
        });
    }

    public void handleDrag(InventoryDragEvent event) {
        Set<Integer> slots = event.getRawSlots();

        //Check that player hasn't tried to put items in the wrong place
        for (int slot : slots) {
            if (slot < 54 && slot >= 45) {
                event.setCancelled(true);
                return;
            }
        }

        //Check that items being added are valid
        if (!event.getNewItems().entrySet().iterator().hasNext()) {
            event.setCancelled(true);
            return;
        }
        Map.Entry<Integer, ItemStack> entry = event.getNewItems().entrySet().iterator().next();
        ItemStack addedItem = entry.getValue();
        String buttonType = getButtonType(addedItem);
        if (buttonType == null) {
            if (!Traincarts.isTraincartsTicket(addedItem)) {
                event.setCancelled(true);
                return;
            }
            //Item is a traincarts ticket, get ticket shop ticket from that
            Ticket convertedTicket = Traincarts.getAsTicketShopTicket(addedItem);
            if (convertedTicket == null) {
                event.setCancelled(true);
                return;
            }
            final ItemStack conertedItem = convertedTicket.getItemStack();

            //Convert the items
            Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
                for (int slot : slots) {
                    if (slot < 45) {
                        event.getInventory().setItem(slot, conertedItem);
                    }
                }
                ((Player) event.getWhoClicked()).updateInventory();
            }, 1L);
        } else if (!buttonType.equals("link") && !buttonType.equals("ticket")) {
            //Item is invalid
            getPlugin().getLogger().severe("Was cancelled 4!");
            event.setCancelled(true);
        }
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();
        String buttonType = getButtonType(clickedItem);
        if (buttonType != null) {
            switch (buttonType) {
                case "next_page", "prev_page", "delete_page", "insert_page" -> {
                    event.setCancelled(true);
                    handleButtonClick(event, buttonType);
                    return;
                }
            }
        }
        //Player has not clicked a button, handle as if they are placing into or removing item from gui
        if (event.getRawSlot() >= 45 && event.getRawSlot() < 54) {
            event.setCancelled(true);
            return;
        }

        if (event.getCursor() == null || event.getCursor().getType() == Material.AIR) {
            return;
        }
        ItemStack addedItem = event.getCursor();
        String addedButtonType = ButtonUtils.getButtonType(addedItem);
        if (addedButtonType != null) {
            if (addedButtonType.equals("link") || addedButtonType.equals("ticket")) {
                return;
            }
            event.setCancelled(true);
            return;
        }
        //Added item is not a ticket shop item
        if (!Traincarts.isTraincartsTicket(addedItem)) {
            event.setCancelled(true);
            return;
        }
        Ticket convertedTicket = Traincarts.getAsTicketShopTicket(addedItem);
        if (convertedTicket == null) {
            event.setCancelled(true);
            return;
        }
        event.getWhoClicked().setItemOnCursor(convertedTicket.getItemStack());
    }
    private void handleButtonClick(InventoryInteractEvent event, String buttonType) {
        wasClosed = false;

        //Save the current page
        PageBuilder pageBuilder = new PageBuilder();
        pageBuilder.addInventory(event.getInventory());
        Button[] page = pageBuilder.getPage();

        removeCursorItem();

        //Now run asynchronous to save the inventory
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            savePageToDatabase(page);
            switch (buttonType) {
                case "next_page" -> {
                    pageBuilder.addNextPageButton();
                    pages.put(getPageNumber(), page);
                    this.nextPage();
                }
                case "prev_page" -> {
                    pageBuilder.addPrevPageButton();
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
        if (getPageNumber() + 1 > getMaxPages()) return;
        setPageNumber(getPageNumber() + 1);
        if (getPageNumber() + 1 >= getTotalPages()) setTotalPages(getPageNumber() + 1);
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
        if (getTotalPages() + 1 > getMaxPages()) {
            return;
        }
        try {
            GuiDataAccessor guiAccessor = new GuiDataAccessor();
            guiAccessor.insertPage(getGuiId(), getPageNumber());
            open();
        } catch (ModificationException e) {
            getPlayer().sendMessage(ChatColor.RED + "Failed to insert page");
            closeInventory();
            getPlugin().handleSqlException(e);
        }
    }
    private void deletePage() {
        ConfirmPageDeleteGui newGui = new ConfirmPageDeleteGui(getGuiId(), getPageNumber(), getPlayer());
        getSession().addGui(newGui);
        newGui.open();
    }

    private void savePageToDatabase(Button[] page) {
        //TODO possible optimization to check if any changes were made before saving?
        List<TicketDatabaseObject> tickets = new ArrayList<>();
        List<LinkDatabaseObject> links = new ArrayList<>();

        for (int slot = 0; slot < page.length - 9; slot++) {
            Button button = page[slot];
            if (button instanceof Ticket ticket) tickets.add(ticket.getAsDatabaseObject(slot));
            if (button instanceof Link link) links.add(link.getAsDatabaseObject(slot));
        }
        try {
            TicketDataAccessor ticketAccessor = new TicketDataAccessor();
            LinkDataAccessor linkAccessor = new LinkDataAccessor();

            ticketAccessor.saveTicketPage(getGuiId(), getPageNumber(), tickets);
            linkAccessor.saveLinkPage(getGuiId(), getPageNumber(), links);
        } catch (ModificationException e) {
            closeInventory();
            getPlugin().handleSqlException(e);
        }
    }
}