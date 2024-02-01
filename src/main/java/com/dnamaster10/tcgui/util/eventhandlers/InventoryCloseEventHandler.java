package com.dnamaster10.tcgui.util.eventhandlers;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

import static com.dnamaster10.tcgui.TraincartsGui.getPlugin;

public class InventoryCloseEventHandler implements Listener {
    @EventHandler
    void onPlayerCloseInventory(InventoryCloseEvent event) {
        getPlugin().getGuiManager().handleInventoryCloseEvent(event);
    }
}
