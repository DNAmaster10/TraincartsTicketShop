package com.dnamaster10.tcgui.objects;

import com.dnamaster10.tcgui.TraincartsGui;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public abstract class Gui {
    private int currentPage;
    private Inventory inventory;
    private String guiName;
    public abstract void open(Player p);
    public abstract void nextPage(Player p);
    public abstract void prevPage(Player p);
    public abstract void handleClick(InventoryClickEvent event, List<ItemStack> items);
    protected Inventory getInventory() {
        return this.inventory;
    }
    protected void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }
    protected int getPage() {
        return this.currentPage;
    }
    protected void setPage(int page) {
        this.currentPage = page;
    }
    protected String getGuiName() {
        return this.guiName;
    }
    protected void setGuiName (String guiName) {
        this.guiName = guiName;
    }
    protected void updateNewInventory(Inventory newInventory) {
        //Takes in an inventory, and replaces all current items with new items
        //Clear the current inventory
        this.inventory.clear();
        for (int i = 0; i < newInventory.getSize(); i++) {
            if (newInventory.getItem(i) != null) {
                this.inventory.setItem(i, newInventory.getItem(i));
            }
        }
    }
}
