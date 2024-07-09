package com.dnamaster10.traincartsticketshop.util.eventhandlers;

import com.dnamaster10.traincartsticketshop.objects.guis.GuiHolder;
import com.dnamaster10.traincartsticketshop.objects.guis.multipageguis.EditGui;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class InventoryCloseEventHandler implements Listener {
    @EventHandler
    void onPlayerCloseInventory(InventoryCloseEvent event) {
        if (!(event.getInventory().getHolder() instanceof GuiHolder g)) return;
        if (g.getGui() instanceof EditGui e) e.handleCloseEvent();
    }
}
