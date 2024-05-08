package com.dnamaster10.traincartsticketshop.util;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

import java.util.HashMap;

public class GuiManager {
    //Holds all current sessions bound to a specific player
    private final HashMap<Player, Session> sessions = new HashMap<>();
    //A hashmap containing all guis which are currently being edited
    private final HashMap<Integer, Player> openEditGuis = new HashMap<>();
    public boolean hasGuiOpen(Player p) {
        Session session = getSession(p);
        if (session == null) return false;
        InventoryView inventoryView = p.getOpenInventory();
        Inventory topInventory = inventoryView.getTopInventory();
        if (topInventory.getType() != InventoryType.CHEST) return false;
        return session.isGuiInventory(topInventory);

    }
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
    public void handleInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Session session = getSession(player);
        if (session == null) return;

        session.handleInventoryClick(event);
    }
    public void handleInventoryDrag(InventoryDragEvent event) {
        Player player = (Player)  event.getWhoClicked();
        Session session = getSession(player);
        if (session == null) return;

        session.handleInventoryDrag(event);
    }
    public void handleInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;

        Session session = getSession(player);

        if (session == null || !isGuiInventory(event.getInventory(), player)) return;

        session.handleInventoryClose();
    }
    public void handlePlayerLeave(Player player) {
        Session session = getSession(player);
        if (session == null) return;

        session.handleInventoryClose();
        closeSession(player);
    }
}
