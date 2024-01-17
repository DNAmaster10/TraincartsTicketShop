package com.dnamaster10.tcgui.util.eventhandlers;

import com.dnamaster10.tcgui.TraincartsGui;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class InventoryClickEventHandler implements Listener {
    @EventHandler
    void onInventoryClick(InventoryClickEvent event) {
        //First get any items which were altered in the event
        List<ItemStack> items = new ArrayList<>();
        if (event.getCurrentItem() != null) {
            items.add(event.getCurrentItem());
        }
        if (event.getClickedInventory() != null && event.getClickedInventory().getItem(event.getSlot()) != null) {
            items.add(event.getClickedInventory().getItem(event.getSlot()));
        }
        if (event.getWhoClicked().getItemOnCursor().getAmount() > 0) {
            items.add(event.getWhoClicked().getItemOnCursor());
        }
        Bukkit.getScheduler().runTaskLater(TraincartsGui.plugin, () -> {
            TraincartsGui.getPlugin().getGuiManager().handleInventoryClickEvent(event, items);
        }, 1L);
    }
}
