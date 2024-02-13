package com.dnamaster10.traincartsticketshop.objects.guis;

import com.dnamaster10.traincartsticketshop.objects.buttons.Button;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

public class InventoryBuilder {
    //Takes in an array of buttons and outputs an inventory
    private final Inventory inventory;

    public Inventory getInventory() {
        return this.inventory;
    }
    public InventoryBuilder(Button[] buttons, String displayName) {
        //Create the inventory object
        inventory = Bukkit.createInventory(null, 54, displayName);

        //Add page contents to the inventory
        for (int slot = 0; slot < 54; slot++) {
            if (buttons[slot] == null) {
                continue;
            }
            inventory.setItem(slot, buttons[slot].getItemStack());
        }
    }
}
