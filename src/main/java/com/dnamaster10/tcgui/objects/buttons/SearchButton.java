package com.dnamaster10.tcgui.objects.buttons;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import static com.dnamaster10.tcgui.TraincartsGui.getPlugin;
import static com.dnamaster10.tcgui.objects.buttons.DataKeys.BUTTON_TYPE;

public class SearchButton extends Button {
    public SearchButton() {
        //Create item
        item = new ItemStack(Material.SPYGLASS, 1);
        ItemMeta meta = item.getItemMeta();

        //Set display
        assert meta != null;
        meta.setDisplayName("Search this gui");

        meta.getPersistentDataContainer().set(BUTTON_TYPE, PersistentDataType.STRING, "search");

        item.setItemMeta(meta);
    }
}
