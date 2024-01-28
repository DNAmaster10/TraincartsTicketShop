package com.dnamaster10.tcgui.objects.buttons;

import com.dnamaster10.tcgui.TraincartsGui;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class PrevPageButton extends Button {
    public PrevPageButton() {
        //Create the item
        item = new ItemStack(Material.BOOK, 1);
        ItemMeta meta = item.getItemMeta();
        assert  meta != null;

        //Set display
        meta.setDisplayName("Prev Page");

        //Set button type
        NamespacedKey buttonKey = new NamespacedKey(TraincartsGui.getPlugin(), "button_type");

        meta.getPersistentDataContainer().set(buttonKey, PersistentDataType.STRING, "prev_page");
        item.setItemMeta(meta);
    }
}
