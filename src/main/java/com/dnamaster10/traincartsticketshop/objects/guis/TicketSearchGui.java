package com.dnamaster10.traincartsticketshop.objects.guis;

import com.dnamaster10.traincartsticketshop.objects.guis.interfaces.ClickHandler;
import com.dnamaster10.traincartsticketshop.objects.guis.interfaces.Pageable;
import com.dnamaster10.traincartsticketshop.util.GuiUtils;
import com.dnamaster10.traincartsticketshop.util.Session;
import com.dnamaster10.traincartsticketshop.util.Utilities;
import com.dnamaster10.traincartsticketshop.util.database.accessors.GuiDataAccessor;
import com.dnamaster10.traincartsticketshop.util.database.accessors.TicketDataAccessor;
import com.dnamaster10.traincartsticketshop.util.exceptions.QueryException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import static com.dnamaster10.traincartsticketshop.TraincartsTicketShop.getPlugin;
import static com.dnamaster10.traincartsticketshop.util.ButtonUtils.getButtonType;

public class TicketSearchGui extends Gui implements InventoryHolder, ClickHandler, Pageable {
    private final int guiId;
    private final PageManager pageManager = new PageManager(0);
    private int totalPages;
    private final String searchTerm;
    private final Player player;
    private String displayName;
    private Inventory inventory;

    public TicketSearchGui(Player player, int guiId, String searchTerm) {
        this.player = player;
        this.guiId = guiId;
        this.searchTerm = searchTerm;

        GuiDataAccessor guiDataAccessor = new GuiDataAccessor();
        TicketDataAccessor ticketDataAccessor = new TicketDataAccessor();

        try {
            totalPages = Utilities.getPageCount(ticketDataAccessor.getTotalTicketSearchResults(guiId, searchTerm), 45);
            displayName = "Searching: " + guiDataAccessor.getDisplayName(guiId);
        } catch (QueryException e) {
            getPlugin().handleSqlException(player, e);
            Bukkit.getScheduler().runTask(getPlugin(), player::closeInventory);
            return;
        }
        pageManager.addPage(0, getNewPage(0));
        inventory = pageManager.getPage(0).getAsInventory(this);
    }

    private Page getNewPage(int pageNumber) {
        Page page = new Page();
        page.setDisplayName(displayName + " (" + (pageNumber + 1) + "/" + totalPages + ")");

        TicketDataAccessor ticketDataAccessor = new TicketDataAccessor();
        try {
            page.addFromTicketDatabaseObjects(ticketDataAccessor.searchTickets(guiId, pageNumber * 45, searchTerm));
        } catch (QueryException e) {
            getPlugin().handleSqlException(player, e);
            Bukkit.getScheduler().runTask(getPlugin(), player::closeInventory);
            return null;
        }

        if (pageNumber + 1 < totalPages) {
            page.addNextPageButton();
        }
        if (pageNumber > 0) {
            page.addPrevPageButton();
        }

        return page;
    }

    @Override
    public void open() {
        Bukkit.getScheduler().runTask(getPlugin(), () -> player.openInventory(inventory));
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();
        String buttonType = getButtonType(clickedItem);
        if (buttonType == null) return;

        switch (buttonType) {
            case "ticket" -> {
                GuiUtils.handleTicketItemPurchase(clickedItem, player);
                Bukkit.getScheduler().runTask(getPlugin(), player::closeInventory);
            }
            case "prev_page" -> prevPage();
            case "next_pave" -> nextPage();
            //TODO add back support for search guis
            case "back" -> {
                Session session = getPlugin().getGuiManager().getSession(player);
                if (!session.checkBack()) return;
                session.back();
            }
        }
    }

    //TODO possibly merge following two methods into "setPage()" method.
    @Override
    public void nextPage() {
        int currentPageNumber = pageManager.getCurrentPageNumber();
        if (currentPageNumber + 1 >= totalPages) return;
        int nextPageNumber = currentPageNumber + 1;
        pageManager.setCurrentPageNumber(nextPageNumber);
        if (!pageManager.hasPage(nextPageNumber)) {
            Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
                Page newPage = getNewPage(nextPageNumber);
                if (newPage == null) return;
                pageManager.addPage(nextPageNumber, newPage);
                inventory = newPage.getAsInventory(this);
                open();
            });
            return;
        }
        inventory = pageManager.getPage(nextPageNumber).getAsInventory(this);
        open();
    }

    @Override
    public void prevPage() {
        int currentPageNumber = pageManager.getCurrentPageNumber();
        if (currentPageNumber <= 0) return;
        int prevPageNumber = currentPageNumber - 1;
        pageManager.setCurrentPageNumber(prevPageNumber);
        if (!pageManager.hasPage(prevPageNumber)) {
            Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
                Page newPage = getNewPage(prevPageNumber);
                if (newPage == null) return;
                pageManager.addPage(prevPageNumber, newPage);
                inventory = newPage.getAsInventory(this);
                open();
            });
            return;
        }
        inventory = pageManager.getPage(prevPageNumber).getAsInventory(this);
        open();
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
