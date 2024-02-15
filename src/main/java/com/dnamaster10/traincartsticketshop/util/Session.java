package com.dnamaster10.traincartsticketshop.util;

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
    private final Stack<Gui> guis = new Stack<>();

    static {
        maxGuis = getPlugin().getConfig().getInt("MaxStoredGuisPerPlayer");
    }

    public Session(Player owner) {
        this.owner = owner;
        if (owner == null) {
            throw new IllegalArgumentException("Owner cannot be null");
        }
    }

    public boolean isGuiInventory(Inventory inventory) {
        //Returns true if the passed inventory matches that of the highest gui on the stack
        if (guis.isEmpty()) {
            return false;
        }
        Inventory guiInventory = guis.peek().getInventory();
        return Objects.equals(inventory, guiInventory);
    }

    public void addGui(Gui gui) {
        guis.push(gui);
        if (gui instanceof EditGui) {
            getPlugin().getGuiManager().addEditGui(gui.getGuiId(), owner);
        }
        if (guis.size() > maxGuis) {
            guis.remove(0);
        }
    }

    public void back() {
        if (guis.size() <= 1) {
            return;
        }
        guis.pop();
        Gui previousGui = guis.peek();
        previousGui.open();
    }

    public boolean checkBack() {
        return guis.size() > 1;
    }

    public void handleInventoryClick(InventoryClickEvent event, ItemStack clickedItem) {
        guis.peek().handleClick(event, clickedItem);
    }

    public void handleInventoryClose() {
        Gui topGui = guis.peek();
        if (topGui instanceof EditGui e) {
            e.handleCloseEvent();
        }
    }
}
