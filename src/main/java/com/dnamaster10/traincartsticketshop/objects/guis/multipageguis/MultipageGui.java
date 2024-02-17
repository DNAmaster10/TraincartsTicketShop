package com.dnamaster10.traincartsticketshop.objects.guis.multipageguis;

import com.dnamaster10.traincartsticketshop.objects.buttons.Button;
import com.dnamaster10.traincartsticketshop.objects.guis.Gui;
import com.dnamaster10.traincartsticketshop.objects.guis.InventoryBuilder;
import com.dnamaster10.traincartsticketshop.util.exceptions.DQLException;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;

import static com.dnamaster10.traincartsticketshop.TraincartsTicketShop.getPlugin;

public abstract class MultipageGui extends Gui {
    //Multipage guis store their inventory contents in an array to save on database queries
    //This hashmap isn't populated with a page until a page is accessed. A HashMap is used instead of a list
    //as it's possible to open guis from signs at a specific page rather than the first.
    //The hashmap holds the page number as the key.
    private final HashMap<Integer, Button[]> pages = new HashMap<>();
    private int currentPage;
    //The highest page number which this gui allows
    private int maxPages;
    protected HashMap<Integer, Button[]> getPages() {
        return pages;
    }
    protected abstract Button[] generateNewPage() throws DQLException;
    protected int getPageNumber() {
        return this.currentPage;
    }
    protected void setPageNumber(int pageNumber) {
        this.currentPage = pageNumber;
    }
    protected int getMaxPages() {
        return this.maxPages;
    }
    protected void setMaxPages(int maxPages) {
        this.maxPages = maxPages;
    }
    protected boolean checkPage(int pageNumber) {
        //Returns true if a page exists
        return pages.containsKey(pageNumber);
    }
    protected Button[] getPage(int pageNumber) {
        return pages.get(pageNumber);
    }
    protected void setPage(int pageNumber, Button[] pageContents) {
        pages.put(pageNumber, pageContents);
    }
    //Generates a new page for this gui.
    protected void nextPage() {
        //Check there are pages beyond this page
        if (this.currentPage + 1 > maxPages) {
            return;
        }
        //If there are, increment the current page
        this.currentPage++;

        //Open the new page
        open();
    }
    protected void prevPage() {
        //Check there are pages before this page
        if (this.currentPage - 1 < 0) {
            return;
        }
        //If there are, decrement the current page
        this.currentPage--;

        //Open the new page
        open();
    }
    @Override
    public void open() {
        //Check if the page exists
        if (!checkPage(getPageNumber())) {
            //If it doesn't, generate a new page asynchronously and open it
            Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
                Button[] newPage;
                try {
                    newPage = generateNewPage();
                } catch (DQLException e) {
                    openErrorGui("An error occurred generating that gui");
                    getPlugin().handleSqlException(getPlayer(), e);
                    return;
                }
                setPage(getPageNumber(), newPage);
                //Inventories must be opened sync
                Bukkit.getScheduler().runTask(getPlugin(), () -> openPage(newPage));
            });
            return;
        }
        //If the page does exist, open it
        openPage(pages.get(currentPage));
    }
    private void openPage(Button[] pageButtons) {
        String pageText = (currentPage + 1) + (" / ") + (maxPages + 1);
        InventoryBuilder inventoryBuilder = new InventoryBuilder(pageButtons, getDisplayName() + " - " + pageText);
        Inventory newInventory = inventoryBuilder.getInventory();
        setInventory(newInventory);
        getPlayer().openInventory(newInventory);
    }
}
