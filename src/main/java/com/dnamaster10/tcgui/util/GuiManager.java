package com.dnamaster10.tcgui.util;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;

public class GuiManager {
    //Holds all current sessions bound to a specific player
    private final HashMap<Player, Session> SESSIONS = new HashMap<>();
    private boolean isGuiInventory(Inventory inventory, Player p) {
        //Returns true if the given inventory is within a session
        Session session = getSession(p);
        if (session == null) {
            return false;
        }
        return (session.isGuiInventory(inventory));
    }
    public Session getSession(Player p) {
        if (!SESSIONS.containsKey(p)) {
            return null;
        }
        return SESSIONS.get(p);
    }
    public void createNewSession(Player p) {
        //Removes a player's old session and replaced it with a new one
        Session newSession = new Session();
        SESSIONS.put(p, newSession);
    }
    public void handleInventoryClick(InventoryClickEvent event, List<ItemStack> items) {
        if (!(event.getWhoClicked() instanceof Player p)) {
            return;
        }
        Session session = getSession(p);
        if (session == null) {
            return;
        }
        if (!isGuiInventory(event.getClickedInventory(), p)) {
            return;
        }

        //Clicked inventory is a gui - handle click
        session.handleInventoryClick(event, items);
    }
    public void handleInventoryClose(InventoryCloseEvent event) {
        Session session = getSession((Player) event.getPlayer());
        if (session != null) {
            session.handleInventoryClose();
        }
    }
}
