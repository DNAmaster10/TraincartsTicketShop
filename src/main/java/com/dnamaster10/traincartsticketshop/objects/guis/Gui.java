package com.dnamaster10.traincartsticketshop.objects.guis;

import com.dnamaster10.traincartsticketshop.util.Session;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

import static com.dnamaster10.traincartsticketshop.TraincartsTicketShop.getPlugin;

public abstract class Gui {
    //The inventory currently open within this gui
    private Inventory inventory;
    private String guiName;
    private int guiId;
    private Player player;
    private String displayName;
    public abstract void open();
    public abstract void handleClick(InventoryClickEvent event, List<ItemStack> items);
    public Inventory getInventory() {
        return this.inventory;
    }
    protected void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }
    public int getGuiId() {
        return this.guiId;
    }
    protected void setGuiId(int id) {
        this.guiId = id;
    }
    protected String getDisplayName() {
        return this.displayName;
    }
    protected void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    protected Player getPlayer() {
        return player;
    }
    protected void setPlayer(Player p) {
        player = p;
    }
    protected void back() {
        removeCursorItem();
        //Check there is a previous gui
        Session currentSession = getSession();
        if (!currentSession.checkBack()) {
            return;
        }
        //If there is, go back
        currentSession.back();
    }
    protected void removeCursorItem() {
        player.setItemOnCursor(null);
    }
    protected void removeCursorItemAndClose() {
        //Removes item on cursor and closes inventory at the same time
        player.setItemOnCursor(null);
        Bukkit.getScheduler().runTaskLater(getPlugin(), () -> player.closeInventory(), 1L);
    }
    protected void openErrorGui(String errorMessage) {
        ErrorGui errorGui = new ErrorGui(errorMessage, player);
        player.setItemOnCursor(null);
        errorGui.open();
    }
    protected Session getSession() {
        return getPlugin().getGuiManager().getSession(getPlayer());
    }
}
