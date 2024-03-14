package com.dnamaster10.traincartsticketshop.util.eventhandlers;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import static com.dnamaster10.traincartsticketshop.TraincartsTicketShop.getPlugin;

public class InventoryClickEventHandler implements Listener {
    @EventHandler(ignoreCancelled = true)
    void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getClickedInventory() == null) return;
        if (!getPlugin().getGuiManager().hasGuiOpen(player)) return;
        if (event.getClickedInventory().getType() != InventoryType.CHEST) return;

        //Get item in the clicked slot
        ItemStack itemStack = event.getCurrentItem();
        getPlugin().getGuiManager().handleInventoryClick(event);
    }
}
