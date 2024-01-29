package com.dnamaster10.tcgui.util.eventhandlers;

import com.dnamaster10.tcgui.TraincartsGui;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class InventoryCloseEventHandler implements Listener {
    @EventHandler
    void onCloseInventory(InventoryCloseEvent event) {
        //TraincartsGui.getPlugin().getGuiManager().handleInventoryCloseEvent((Player) event.getPlayer());
    }
}
