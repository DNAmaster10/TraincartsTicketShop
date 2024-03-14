package com.dnamaster10.traincartsticketshop.util.eventhandlers;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;

import static com.dnamaster10.traincartsticketshop.TraincartsTicketShop.getPlugin;

public class InventoryDragEventHandler implements Listener {
    @EventHandler(ignoreCancelled = true)
    void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getInventory().getType() != InventoryType.CHEST) return;
        if (!getPlugin().getGuiManager().hasGuiOpen(player)) return;
        for (int slot : event.getRawSlots()) {
            if (slot < 54) {
                getPlugin().getGuiManager().handleInventoryDrag(event);
                return;
            }
        }
    }
}
