package com.dnamaster10.traincartsticketshop.util;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class GuiManager {
    //Holds all current sessions bound to a specific player
    private final HashMap<Player, Session> sessions = new HashMap<>();
    //A hashmap containing all guis which are currently being edited
    private final HashMap<Integer, Player> openEditGuis = new HashMap<>();
    private boolean isGuiInventory(Inventory inventory, Player player) {
        //Returns true if the given inventory is within a session
        Session session = getSession(player);
        return session != null && session.isGuiInventory(inventory);
    }
    public Session getSession(Player player) {
        return sessions.get(player);
    }
    public Session getNewSession(Player player) {
        //Creates a new session, removes a player's old session, and returns the new session
        Session newSession = new Session(player);
        sessions.put(player, newSession);
        return newSession;
    }
    public void closeSession(Player player) {
        sessions.remove(player);
        openEditGuis.values().remove(player);
    }
    public void addEditGui(int guiId, Player player) {
        openEditGuis.put(guiId, player);
    }
    public void removeEditGui(int guiId) {
        openEditGuis.remove(guiId);
    }
    public Player getGuiEditor(int guiId) {
        //Returns the player who is editing a gui
        return openEditGuis.get(guiId);
    }
    public void handleInventoryClick(InventoryClickEvent event, ItemStack clickedItem) {
        Player player = (Player) event.getWhoClicked();
        Session session = getSession(player);
        if (session == null || !isGuiInventory(event.getClickedInventory(), player)) {
            return;
        }
        session.handleInventoryClick(event, clickedItem);
    }
    public void handleInventoryClose(InventoryCloseEvent event) {
        Session session = getSession((Player) event.getPlayer());
        if (session != null) {
            session.handleInventoryClose();
        }
    }
    public void handlePlayerLeave(Player player) {
        Session session = getSession(player);
        if (session == null) return;

        session.handleInventoryClose();
        closeSession(player);
    }
}
