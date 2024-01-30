package com.dnamaster10.tcgui.objects;

import com.dnamaster10.tcgui.TraincartsGui;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class Gui {
    private int currentPage;
    private Inventory inventory;
    private String guiName;
    private int guiId;
    private Player player;
    private String displayName;
    public abstract void open();
    protected abstract void generate() throws SQLException;
    protected abstract void nextPage();
    protected abstract void prevPage();
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
    protected int getGuiId() {
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
    protected TraincartsGui getPlugin() {
        return TraincartsGui.getPlugin();
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
    protected String getButtonType(ItemStack button) {
        //First check if item is a button
        NamespacedKey key = new NamespacedKey(getPlugin(), "button_type");
        if (!button.hasItemMeta()) {
            return null;
        }
        if (!Objects.requireNonNull(button.getItemMeta()).getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
            return null;
        }
        return button.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING);
    }
}
