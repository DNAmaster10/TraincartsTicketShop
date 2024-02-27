package com.dnamaster10.traincartsticketshop.util.eventhandlers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import static com.dnamaster10.traincartsticketshop.TraincartsTicketShop.getPlugin;

public class InventoryClickEventHandler implements Listener {
    @EventHandler
    void onInventoryClick(InventoryClickEvent event) {
        //Check that the clicker is a player
        if (!(event.getWhoClicked() instanceof Player)) return;

        //Check the player clicked a gui and not their own inventory
        if (event.getClickedInventory() == null) return;
        if (event.getClickedInventory().getType() != InventoryType.CHEST) return;

        //Get item which was clicked
        ItemStack item = event.getCurrentItem();
        if (item == null) return;

        //TODO probs shouldn't be a runlater task.
        Bukkit.getScheduler().runTaskLater(getPlugin(), () -> getPlugin().getGuiManager().handleInventoryClick(event, item), 1L);
    }
}
