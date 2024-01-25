package com.dnamaster10.tcgui.objects;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class SearchGui extends Gui {
    @Override
    public void open(Player p) {
        //Opens the gui to the player
        if (Bukkit.isPrimaryThread()) {
            p.openInventory(getInventory());
            return;
        }
        Bukkit.getScheduler().runTask(getPlugin(), () -> {
            p.openInventory(getInventory());
        });
    }

    @Override
    public void nextPage(Player p) {

    }

    @Override
    public void prevPage(Player p) {

    }

    @Override
    public void handleClick(InventoryClickEvent event, List<ItemStack> items) {

    }
    //Opened when the compass item is clicked and a gui is searched
}
