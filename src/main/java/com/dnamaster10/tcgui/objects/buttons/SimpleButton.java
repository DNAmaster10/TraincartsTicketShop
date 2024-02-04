package com.dnamaster10.tcgui.objects.buttons;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;

import static com.dnamaster10.tcgui.objects.buttons.DataKeys.BUTTON_TYPE;

public class SimpleButton extends Button {
    //For simple buttons which only use a "button_type" tag
    //eg a "next page" button
    public SimpleButton(String buttonType, Material material, String displayText) {
        //Create the item
        item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;

        //Set display text
        meta.setDisplayName(displayText);

        //Set type tag
        meta.getPersistentDataContainer().set(BUTTON_TYPE, PersistentDataType.STRING, buttonType);
        item.setItemMeta(meta);
    }
    public SimpleButton(String buttonType, HeadData.HeadType headType, String displayText) {
        //For simple buttons which use heads instead
        item = HeadData.getPlayerHeadItem(headType);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;

        //Set display text
        meta.setDisplayName(displayText);

        //Set type tag
        meta.getPersistentDataContainer().set(BUTTON_TYPE, PersistentDataType.STRING, buttonType);
        item.setItemMeta(meta);
    }
}
