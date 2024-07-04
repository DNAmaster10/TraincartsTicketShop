package com.dnamaster10.traincartsticketshop.objects.buttons;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import static com.dnamaster10.traincartsticketshop.objects.buttons.DataKeys.BUTTON_TYPE;

public class SimpleItemButton extends Button {
    //For simple buttons using an item
    private final String buttonType;
    private final Material material;
    private final String displayText;

    @Override
    public ItemStack getItemStack() {
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;

        meta.setDisplayName(this.displayText);
        meta.getPersistentDataContainer().set(BUTTON_TYPE, PersistentDataType.STRING, this.buttonType);
        item.setItemMeta(meta);

        return item;
    }

    public SimpleItemButton(String buttonType, Material material, String displayText) {
        this.buttonType = buttonType;
        this.material = material;
        this.displayText = displayText;
    }
}
