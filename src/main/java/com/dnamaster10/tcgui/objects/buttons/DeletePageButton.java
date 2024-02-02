package com.dnamaster10.tcgui.objects.buttons;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import static com.dnamaster10.tcgui.objects.buttons.DataKeys.BUTTON_TYPE;

public class DeletePageButton extends Button {
    public DeletePageButton() {
        //Create the item
        item = new ItemStack(Material.BARRIER, 1);
        ItemMeta meta = item.getItemMeta();

        //Set display
        assert meta != null;
        meta.setDisplayName("Delete page");

        //Set button data
        meta.getPersistentDataContainer().set(BUTTON_TYPE, PersistentDataType.STRING, "delete_page");
        item.setItemMeta(meta);
    }
}
