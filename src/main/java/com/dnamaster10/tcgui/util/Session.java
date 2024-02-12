package com.dnamaster10.tcgui.util;

import com.dnamaster10.tcgui.objects.guis.EditGui;
import com.dnamaster10.tcgui.objects.guis.Gui;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;
import java.util.Stack;

import static com.dnamaster10.tcgui.TraincartsGui.getPlugin;

public class Session {
    //Holds information about a player's current gui session
    //Indicates the maximum amount of guis a player can have stored in their session stack
    private static final int maxGuis;

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
    }
    private void removeTopGui() {
        GUIS.pop();
    }
    public void back() {
        //Check that there is a gui before this one
        if (GUIS.size() <= 1) {
            return;
        }

        //Open the last gui behind the current one and remove the current one
        GUIS.pop();
        Gui newGui = GUIS.peek();
        newGui.open();
    }
    public boolean checkBack() {
        //Returns true if there is a gui before the current open gui
        return GUIS.size() > 1;
    }
    public void handleInventoryClick(InventoryClickEvent event, List<ItemStack> items) {
        GUIS.peek().handleClick(event, items);
    }
    public void handleInventoryClose() {
        Gui topGui = GUIS.peek();
        if (topGui instanceof EditGui e) {
            e.handleCloseEvent();
        }
    }
}