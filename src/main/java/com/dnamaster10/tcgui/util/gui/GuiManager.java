package com.dnamaster10.tcgui.util.gui;

import com.dnamaster10.tcgui.objects.EditGui;
import com.dnamaster10.tcgui.objects.Gui;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import static com.dnamaster10.tcgui.TraincartsGui.getPlugin;

public class GuiManager {
    //Determines the maximum amount of guis a player can have open until they start to be removed. Used for the back button.
    private final int maxGuis;
    //Holds all currently opened GUIs in a hashmap linking the player who has the gui open and all guis they have opened in current gui "session"
    private final HashMap<Player, Stack<Gui>> GUIS = new HashMap<>();
    public void addGui(Player p, Gui gui) {
        if (GUIS.containsKey(p)) {
            Stack<Gui> guiStack = GUIS.get(p);
            guiStack.push(gui);
            if (guiStack.size() > maxGuis) {
                guiStack.remove(0);
            }
            return;
        }
        GUIS.put(p, new Stack<>());
        GUIS.get(p).push(gui);
    }
    public void back(Player p) {
        //Check there is a previous gui
        if (!GUIS.containsKey(p)) {
            return;
        }
        Stack<Gui> guiStack = GUIS.get(p);
        if (guiStack.size() == 1) {
            //The only gui present is the current open gui so we cant go back
            return;
        }
        //Remove the current gui
        guiStack.pop();
        Gui gui = guiStack.peek();
        gui.open();
    }
    public boolean checkLastGui(Player p) {
        //Returns true if there is more than 1 gui registered to a player already
        if (!GUIS.containsKey(p)) {
            return false;
        }
        return !GUIS.get(p).isEmpty();
    }
    public void closeGuis(Player p) {
        //Removes all guis for the given player
        GUIS.remove(p);
    }
    public void handleInventoryClick(InventoryClickEvent event, List<ItemStack> items) {
        Player p = (Player) event.getWhoClicked();
        //If gui is a tcgui
        if (!GUIS.containsKey(p)) {
            return;
        }
        GUIS.get(p).peek().handleClick(event, items);
    }
    public void handleInventoryCloseEvent(Player p) {
        if (!GUIS.containsKey(p)) {
            return;
        }
        Gui gui = GUIS.get(p).peek();
        if (gui instanceof EditGui g) {
            Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), g::save);
        }
        closeGuis(p);
    }
    public void clearGuis(Player p) {
        //Removes all gui data currently linked to that player. Should be called before starting a new session.
        closeGuis(p);
    }
    public void handleLeaveEvent(Player p) {
        closeGuis(p);
    }
    public GuiManager() {
        this.maxGuis = getPlugin().getConfig().getInt("MaxStoredGuisPerPlayer");
    }
}
