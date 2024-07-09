package com.dnamaster10.traincartsticketshop.objects.guis;

import com.dnamaster10.traincartsticketshop.objects.buttons.Button;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class InventoryBuilder {
    //TODO possible change to inventory.setStorageContents(ItemStack[] items)
    //Takes in an array of buttons and outputs an inventory
    private final Inventory inventory;

    public Inventory getInventory() {
        return this.inventory;
    }
    public InventoryBuilder(InventoryHolder holder, Button[] buttons, String displayName) {
        //Create the inventory object
        inventory = Bukkit.createInventory(holder, 54, displayName);

        //Add page contents to the inventory
        for (int slot = 0; slot < 54; slot++) {
            if (buttons[slot] == null) {
                continue;
            }
            inventory.setItem(slot, buttons[slot].getItemStack());
        }
    }
}
