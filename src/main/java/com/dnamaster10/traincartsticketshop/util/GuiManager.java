package com.dnamaster10.traincartsticketshop.util;

import org.bukkit.entity.Player;

import java.util.HashMap;

public class GuiManager {
    //Holds all current sessions bound to a specific player
    private final HashMap<Player, Session> sessions = new HashMap<>();
    //A hashmap containing all guis which are currently being edited
    private final HashMap<Integer, Player> openEditGuis = new HashMap<>();

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
    public void handlePlayerLeave(Player player) {
        Session session = getSession(player);
        if (session == null) return;
        closeSession(player);
    }
}
