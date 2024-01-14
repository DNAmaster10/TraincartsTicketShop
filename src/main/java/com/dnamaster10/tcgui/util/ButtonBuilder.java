package com.dnamaster10.tcgui.util;

import com.dnamaster10.tcgui.TraincartsGui;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class ButtonBuilder {
    //For creating UI buttons
    public ItemStack getNextPageButton() {
        //Create the item
        ItemStack nextPageItem = new ItemStack(Material.BOOK, 1);
        ItemMeta meta = nextPageItem.getItemMeta();
        assert meta != null;

        //Set display text
        meta.setDisplayName("Next Page");

        //Set button type
        NamespacedKey key = new NamespacedKey(TraincartsGui.plugin, "button_type");
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, "next_page");
        nextPageItem.setItemMeta(meta);

        return nextPageItem;
    }
}
