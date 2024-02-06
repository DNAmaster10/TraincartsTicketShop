package com.dnamaster10.tcgui.objects.guis;

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
    public void open() {
        //Defaults to opening async so that even if someone forgets to run async the server won't lag.
        //Can always be overwritten to make synchronous if no database calls are needed.
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            try {
                generate();
            } catch (SQLException e) {
                removeCursorItemAndClose();
                getPlugin().reportSqlError(getPlayer(), e);
                return;
            }
            Bukkit.getScheduler().runTask(getPlugin(), () -> getPlayer().openInventory(getInventory()));
        });
    }
    protected abstract void generate() throws SQLException;
    public abstract void handleClick(InventoryClickEvent event, List<ItemStack> items);
    protected void back() {
        removeCursorItem();
        //Check there is a previous gui
        if (!getPlugin().getGuiManager().checkLastGui(getPlayer())) {
            return;
        }
        //If there is, go back
        getPlugin().getGuiManager().back(getPlayer());
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
    protected void openErrorGui(String errorMessage) {
        ErrorGui errorGui = new ErrorGui(errorMessage, player);
        player.setItemOnCursor(null);
        errorGui.open();
    }
}
