package com.dnamaster10.traincartsticketshop.util;

import com.dnamaster10.traincartsticketshop.objects.guis.Gui;
import org.bukkit.entity.Player;

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

    public void addGui(Gui gui) {
        guis.push(gui);
        if (guis.size() > maxGuis) {
            guis.remove(0);
        }
    }

    public void back() {
        if (guis.size() <= 1) {
            owner.closeInventory();
            return;
        }
        guis.pop();
        Gui previousGui = guis.peek();
        previousGui.open();
    }

    public boolean checkBack() {
        return guis.size() > 1;
    }
}
