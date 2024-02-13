package com.dnamaster10.tcgui.objects.guis;

import com.dnamaster10.tcgui.objects.buttons.Button;
import com.dnamaster10.tcgui.util.Session;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

import static com.dnamaster10.tcgui.TraincartsGui.getPlugin;

public abstract class Gui {
    private Inventory inventory;
    private String guiName;
    private int guiId;
    private Player player;
    private String displayName;
    public abstract void open();
    public abstract void handleClick(InventoryClickEvent event, List<ItemStack> items);
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
    public Inventory getInventory() {
        return this.inventory;
    }
    protected void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }
    protected String getGuiName() {
        return this.guiName;
    }
    protected void setGuiName (String guiName) {
        this.guiName = guiName;
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
    protected void removeCursorItem() {
        player.setItemOnCursor(null);
    }
    protected void removeCursorItemAndClose() {
        //Removes item on cursor and closes inventory at the same time
        player.setItemOnCursor(null);
        Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
            player.closeInventory();}, 1L);
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
