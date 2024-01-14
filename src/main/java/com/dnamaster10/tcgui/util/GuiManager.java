package com.dnamaster10.tcgui.util;

import com.dnamaster10.tcgui.objects.EditGui;
import com.dnamaster10.tcgui.objects.ShopGui;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class GuiManager {
    //Holds all currently opened GUIs in a hashmap linking the player who
    //currently has the GUi open, and the GUI object.
    private static final HashMap<Player, EditGui> EDIT_GUIS = new HashMap<>();
    private static final HashMap<Player, ShopGui> SHOP_GUIS = new HashMap<>();

    public void registerNewEditGui(EditGui gui, Player p) {
        //If player already has a GUI registered, remove their old one
        EDIT_GUIS.remove(p);
        SHOP_GUIS.remove(p);

        EDIT_GUIS.put(p, gui);
    }
    private void removeGui(Player p) {
        EDIT_GUIS.remove(p);
        SHOP_GUIS.remove(p);
    }
    public void handleInventoryCloseEvent(Player p) {
        //If the closed inventory appears in the hashmaps, remove it
        removeGui(p);
    }
    public void handleLeaveEvent(Player p) {
        removeGui(p);
    }
}
