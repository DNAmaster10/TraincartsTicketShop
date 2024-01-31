package com.dnamaster10.tcgui.objects.buttons;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import static com.dnamaster10.tcgui.TraincartsGui.getPlugin;

public class SearchTicketsButton extends Button {
    public SearchTicketsButton() {
        //Create the item
        item = new ItemStack(Material.PAPER, 1);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;

        //Set display
        meta.setDisplayName("Search tickets");

        //Set button type
        NamespacedKey buttonKey = new NamespacedKey(getPlugin(), "button_type");

        meta.getPersistentDataContainer().set(buttonKey, PersistentDataType.STRING, "search_tickets");
        item.setItemMeta(meta);
    }
}
