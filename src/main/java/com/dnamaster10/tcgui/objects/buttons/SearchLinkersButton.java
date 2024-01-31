package com.dnamaster10.tcgui.objects.buttons;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import static com.dnamaster10.tcgui.TraincartsGui.getPlugin;

public class SearchLinkersButton extends Button {
    public SearchLinkersButton() {
        //Create the item
        item = new ItemStack(Material.ENCHANTED_BOOK, 1);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;

        //Set display
        meta.setDisplayName("Search linkers");

        //Set button type
        NamespacedKey buttonKey = new NamespacedKey(getPlugin(), "button_type");

        meta.getPersistentDataContainer().set(buttonKey, PersistentDataType.STRING, "search_linkers");
        item.setItemMeta(meta);
    }
}
