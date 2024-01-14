package com.dnamaster10.tcgui.objects;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public abstract class Gui {
    private int currentPage;
    private Inventory inventory;
    public abstract void open(Player p);
    public abstract void nextPage(Player p);
    public abstract void prevPage(Player p);
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
}
