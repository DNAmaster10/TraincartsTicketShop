package com.dnamaster10.traincartsticketshop.util;

import com.dnamaster10.traincartsticketshop.objects.guis.ErrorGui;
import com.dnamaster10.traincartsticketshop.objects.guis.multipageguis.EditGui;
import com.dnamaster10.traincartsticketshop.objects.guis.Gui;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.Stack;

import static com.dnamaster10.traincartsticketshop.TraincartsTicketShop.getPlugin;

public class Session {
    //Holds information about a player's current gui session
    //Indicates the maximum amount of guis a player can have stored in their session stack
    private static final int maxGuis;
    private final Player owner;

    static {
        maxGuis = getPlugin().getConfig().getInt("MaxStoredGuisPerPlayer");
    }
    private final Stack<Gui> GUIS = new Stack<>();
    public boolean isGuiInventory(Inventory inventory) {
        //Returns true if the passed inventory matches that of the highest gui on the stack
        if (GUIS.isEmpty()) {
            return false;
        }
        Inventory guiInventory = GUIS.peek().getInventory();
        return Objects.equals(inventory, guiInventory);
    }
    public void addGui(Gui gui) {
        GUIS.push(gui);
        //If edit gui, add to gui edit list in gui manager
        if (gui instanceof EditGui) {
            getPlugin().getGuiManager().addEditGui(gui.getGuiId(), owner);
        }
    }
    public void back() {
        //Check that there is a gui before this one
        if (GUIS.size() <= 1) {
            return;
        }

        //Open the last gui behind the current one and remove the current one
        GUIS.pop();
        Gui previousGui = GUIS.peek();
        previousGui.open();
    }
    public boolean checkBack() {
        //Returns true if there is a gui before the current open gui
        return GUIS.size() > 1;
    }
    public void handleInventoryClick(InventoryClickEvent event, ItemStack clickedItem) {
        GUIS.peek().handleClick(event, clickedItem);
    }
    public void handleInventoryClose() {
        Gui topGui = GUIS.peek();
        if (topGui instanceof EditGui e) {
            e.handleCloseEvent();
        }
    }
    public Session(Player owner) {
        this.owner = owner;
    }
}
