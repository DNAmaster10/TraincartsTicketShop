package com.dnamaster10.traincartsticketshop.objects.guis;

import com.dnamaster10.traincartsticketshop.objects.guis.interfaces.ClickHandler;
import com.dnamaster10.traincartsticketshop.objects.guis.interfaces.Pageable;
import com.dnamaster10.traincartsticketshop.util.Session;
import com.dnamaster10.traincartsticketshop.util.Utilities;
import com.dnamaster10.traincartsticketshop.util.database.accessors.GuiDataAccessor;
import com.dnamaster10.traincartsticketshop.util.database.accessors.LinkDataAccessor;
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
import static com.dnamaster10.traincartsticketshop.util.GuiUtils.linkGui;

/**
 * A Gui used to search the links within a shop.
 */
public class LinkSearchGui extends Gui implements InventoryHolder, ClickHandler, Pageable {
    private final int guiId;
    private final PageManager pageManager = new PageManager(0);
    private int totalPages;
    private final String searchTerm;
    private final Player player;
    private String displayName;
    private Inventory inventory;

    /**
     * @param player The player who will open the Gui
     * @param guiId The ID of the Gui to search
     * @param searchTerm The search term to be searched
     */
    public LinkSearchGui(Player player, int guiId, String searchTerm) {
        this.player = player;
        this.guiId = guiId;
        this.searchTerm = searchTerm;

        getPlugin().getGuiManager().getSession(player).addGui(this);

        GuiDataAccessor guiDataAccessor = new GuiDataAccessor();
        LinkDataAccessor linkDataAccessor = new LinkDataAccessor();

        try {
            totalPages = Utilities.getPageCount(linkDataAccessor.getTotalLinkSearchResults(guiId, searchTerm), 45);
            displayName = "Searching: " + guiDataAccessor.getDisplayName(guiId);
        } catch (QueryException e) {
            getPlugin().handleSqlException(e);
            getPlugin().getServer().getScheduler().runTask(getPlugin(), () -> player.closeInventory());
            return;
        }
        pageManager.addPage(0, getNewPage(0));
        inventory = pageManager.getPage(0).getAsInventory(this);
    }

    private Page getNewPage(int pageNumber) {
        Page page = new Page();
        page.setDisplayName(Utilities.parseColour(displayName + " (" + (pageNumber + 1) + "/" + totalPages + ")"));

        LinkDataAccessor linkDataAccessor = new LinkDataAccessor();
        try {
            page.addFromLinkDatabaseObjects(linkDataAccessor.searchLinks(guiId, pageNumber * 45, searchTerm));
        } catch (QueryException e) {
            getPlugin().handleSqlException(player, e);
            getPlugin().getServer().getScheduler().runTask(getPlugin(), () -> player.closeInventory());
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
            case "link" -> linkGui(clickedItem, player);
            case "prev_page" -> prevPage();
            case "next_page" -> nextPage();
            //TODO add back button support for search guis
            case "back" -> {
                Session session = getPlugin().getGuiManager().getSession(player);
                if (!session.checkBack()) return;
                session.back();
            }
        }
    }

    @Override
    public void nextPage() {
        int currentPageNumber = pageManager.getCurrentPageNumber();
        if (currentPageNumber + 1 >= totalPages) return;
        setPageNumber(currentPageNumber + 1);
    }

    @Override
    public void prevPage() {
        int currentPageNumber = pageManager.getCurrentPageNumber();
        if (currentPageNumber <= 0) return;
        setPageNumber(currentPageNumber - 1);
    }

    private void setPageNumber(int pageNumber) {
        pageManager.setCurrentPageNumber(pageNumber);
        if (!pageManager.hasPage(pageNumber)) {
            Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
                Page newPage = getNewPage(pageNumber);
                if (newPage == null) return;
                pageManager.addPage(pageNumber, newPage);
                inventory = newPage.getAsInventory(this);
                open();
            });
            return;
        }
        inventory = pageManager.getPage(pageNumber).getAsInventory(this);
        open();
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
