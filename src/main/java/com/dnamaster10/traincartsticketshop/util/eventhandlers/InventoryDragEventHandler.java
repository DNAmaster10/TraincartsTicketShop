package com.dnamaster10.traincartsticketshop.util.eventhandlers;

import com.dnamaster10.traincartsticketshop.objects.guis.GuiHolder;
import com.dnamaster10.traincartsticketshop.objects.guis.multipageguis.EditGui;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryDragEvent;

public class InventoryDragEventHandler implements Listener {
    @EventHandler(ignoreCancelled = true)
    void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getInventory().getHolder() instanceof GuiHolder g)) return;
        for (int slot : event.getRawSlots()) {
            if (slot < 54) {
                if (g.getGui() instanceof EditGui e) e.handleDrag(event);
                else event.setCancelled(true);
            }
        }
    }
}
