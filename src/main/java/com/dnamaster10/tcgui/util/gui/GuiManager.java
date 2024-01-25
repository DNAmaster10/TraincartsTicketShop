package com.dnamaster10.tcgui.util.gui;

import com.dnamaster10.tcgui.TraincartsGui;
import com.dnamaster10.tcgui.objects.EditGui;
import com.dnamaster10.tcgui.objects.ShopGui;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Stack;

public class GuiManager {
    //Holds all currently opened GUIs in a hashmap linking the player who
    //currently has the GUi open, and the GUI object.
    private static final HashMap<Player, EditGui> EDIT_GUIS = new HashMap<>();
    private static final HashMap<Player, ShopGui> SHOP_GUIS = new HashMap<>();

    //Used to store the last guis that the player was interacting with. Used for the gui back button.
    private static final HashMap<Player, Stack<LastGui>> PREVIOUS_GUIS = new HashMap<>();

    public void registerNewEditGui(EditGui gui, Player p) {
        //If player already has a GUI registered, remove their old one
        EDIT_GUIS.remove(p);
        SHOP_GUIS.remove(p);

        EDIT_GUIS.put(p, gui);
    }
    public void registerNewShopGui(ShopGui gui, Player p) {
        //If player already has a GUI registered remove their old onw
        EDIT_GUIS.remove(p);
        SHOP_GUIS.remove(p);

        SHOP_GUIS.put(p, gui);
    }
    private void closeGui(Player p) {
        //Removes all stored guis and backs
        EDIT_GUIS.remove(p);
        SHOP_GUIS.remove(p);
        PREVIOUS_GUIS.remove(p);
    }
    public void addPrevGui(String guiName, int guiPage, Player p) {
        if (PREVIOUS_GUIS.containsKey(p)) {
            PREVIOUS_GUIS.get(p).push(new LastGui(guiName, guiPage));
            return;
        }
        PREVIOUS_GUIS.put(p, new Stack<>());
        PREVIOUS_GUIS.get(p).push(new LastGui(guiName, guiPage));
    }
    public boolean checkPrevGui(Player p) {
        if (PREVIOUS_GUIS.containsKey(p)) {
            return !PREVIOUS_GUIS.get(p).isEmpty();
        }
        return false;
    }
    public LastGui getPrevGui(Player p) {
        //Returns the last gui the player was on before the current one and also removes it from the queue
        return PREVIOUS_GUIS.get(p).pop();
    }
    public void handleInventoryClickEvent(InventoryClickEvent event, List<ItemStack> items) {
        Player p = (Player) event.getWhoClicked();
        //If gui is a tcgui gui
        if (EDIT_GUIS.containsKey(p)) {
            EDIT_GUIS.get(p).handleClick(event, items);
        }
        else if (SHOP_GUIS.containsKey(p)) {
            SHOP_GUIS.get(p).handleClick(event, items);
        }
        return;
    }
    public void handleInventoryCloseEvent(Player p) {
        //If the closed gui was an edit gui, we need to save the contents of the GUI to the database
        if (EDIT_GUIS.containsKey(p)) {
            //Save asynchronously
            //Gui must be assigned to a new variable since it will be removed from the hashmap before
            //the async task can even begin.
            EditGui gui = EDIT_GUIS.get(p);
            Bukkit.getScheduler().runTaskAsynchronously(TraincartsGui.getPlugin(), gui::save);
        }
        //If the closed inventory appears in the hashmaps, remove it
        closeGui(p);
    }
    public void handleLeaveEvent(Player p) {
        closeGui(p);
    }
}
