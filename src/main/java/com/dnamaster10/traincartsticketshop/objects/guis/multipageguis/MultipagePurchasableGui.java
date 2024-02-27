package com.dnamaster10.traincartsticketshop.objects.guis.multipageguis;

import com.dnamaster10.traincartsticketshop.objects.buttons.Button;
import com.dnamaster10.traincartsticketshop.objects.guis.InventoryBuilder;
import com.dnamaster10.traincartsticketshop.objects.guis.SearchSelectGui;
import com.dnamaster10.traincartsticketshop.util.GuiUtils;
import com.dnamaster10.traincartsticketshop.util.exceptions.DQLException;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

import static com.dnamaster10.traincartsticketshop.TraincartsTicketShop.getPlugin;
import static com.dnamaster10.traincartsticketshop.util.ButtonUtils.getButtonType;

public abstract class MultipagePurchasableGui extends MultipageGui {

    private final HashMap<Integer, Button[]> pages = new HashMap<>();
    protected abstract Button[] getNewPage() throws DQLException;
    @Override
    public void open() {
        if (pages.containsKey(getPageNumber())) {
            openPage(pages.get(getPageNumber()));
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
    private void openPage(Button[] page) {
        String pageText = "(" + (getPageNumber() + 1) + "/" + (getTotalPages() + 1) + ")";
        InventoryBuilder inventoryBuilder = new InventoryBuilder(page, getDisplayName() + " " + pageText);
        setInventory(inventoryBuilder.getInventory());
        Bukkit.getScheduler().runTaskLater(getPlugin(), () -> getPlayer().openInventory(getInventory()), 1L);
    }
    @Override
    public void handleClick(InventoryClickEvent event, ItemStack clickedItem) {
        String buttonType = getButtonType(clickedItem);
        if (buttonType == null) return;

        getPlayer().setItemOnCursor(null);
        switch (buttonType) {
            case "ticket" -> {
                GuiUtils.handleTicketItemPurchase(clickedItem, getPlayer());
                closeInventory();
            }
            case "prev_page" -> prevPage();
            case "next_page" -> nextPage();
            case "linker" -> link(clickedItem);
            case "back" -> back();
            case "search" -> search();
        }
    }
    @Override
    protected void nextPage() {
        if (this.getPageNumber() + 1 > getTotalPages()) return;
        setPageNumber(getPageNumber() + 1);
        open();
    }
    @Override
    protected void prevPage() {
        if (getPageNumber() - 1 < 0) return;
        this.setPageNumber(getPageNumber() - 1);
        open();
    }
    protected void search() {
        SearchSelectGui gui = new SearchSelectGui(getGuiId(), getPlayer());
        getSession().addGui(gui);
        gui.open();
    }
}
