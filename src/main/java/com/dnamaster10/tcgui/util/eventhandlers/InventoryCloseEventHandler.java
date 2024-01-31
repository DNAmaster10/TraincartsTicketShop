package com.dnamaster10.tcgui.util.eventhandlers;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

import static com.dnamaster10.tcgui.TraincartsGui.getPlugin;

public class InventoryCloseEventHandler implements Listener {
    @EventHandler
    void onPlayerCloseInventory(InventoryCloseEvent event) {
        if (event.getPlayer() instanceof Player p) {
            getPlugin().getGuiManager().handleInventoryCloseEvent(p);
        }
    }
}
