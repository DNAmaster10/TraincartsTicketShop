package com.dnamaster10.traincartsticketshop.util.eventhandlers;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

import static com.dnamaster10.traincartsticketshop.TraincartsTicketShop.getPlugin;

public class InventoryCloseEventHandler implements Listener {
    @EventHandler
    void onPlayerCloseInventory(InventoryCloseEvent event) {
        getPlugin().getGuiManager().handleInventoryClose(event);
    }
}
