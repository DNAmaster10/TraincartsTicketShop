package com.dnamaster10.traincartsticketshop.objects.guis;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class GuiHolder implements InventoryHolder {
    //The inventory holder shared between all guis. Used in event handlers to check whether an inventory is a gui or not.

    private final Gui gui;

    public GuiHolder(Gui gui) {
        this.gui = gui;
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return gui.getInventory();
    }

    public Gui getGui () {
        return this.gui;
    }
}
