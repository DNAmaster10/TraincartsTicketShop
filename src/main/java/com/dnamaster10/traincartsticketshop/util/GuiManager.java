package com.dnamaster10.traincartsticketshop.util;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

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
    public Session getNewSession(Player p) {
        //Creates a new session, removes a player's old session, and returns the new session
        Session newSession = new Session();
        SESSIONS.put(p, newSession);
        return newSession;
    }
    public void closeSession(Player player) {
        SESSIONS.remove(player);
    }
    public void handleInventoryClick(InventoryClickEvent event, ItemStack clickedItem) {
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
        session.handleInventoryClick(event, clickedItem);
    }
    public void handleInventoryClose(InventoryCloseEvent event) {
        Session session = getSession((Player) event.getPlayer());
        if (session != null) {
            session.handleInventoryClose();
        }
    }
    public void handlePlayerLeave(Player player) {
        //The only thing which needs to happen here is to save any open edit gui. This can be handled
        //the same way as an inventory close event.
        getSession(player).handleInventoryClose();

        //Delete the session
        closeSession(player);
    }
}
