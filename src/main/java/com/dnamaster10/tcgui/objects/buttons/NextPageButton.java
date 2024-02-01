package com.dnamaster10.tcgui.objects.buttons;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import static com.dnamaster10.tcgui.TraincartsGui.getPlugin;
import static com.dnamaster10.tcgui.objects.buttons.DataKeys.BUTTON_TYPE;

public class NextPageButton extends Button {
    public NextPageButton() {
        //Create the item
        item = new ItemStack(Material.BOOK, 1);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;

        //Set display text
        meta.setDisplayName("Next Page");

        meta.getPersistentDataContainer().set(BUTTON_TYPE, PersistentDataType.STRING, "next_page");
        item.setItemMeta(meta);
    }
}
