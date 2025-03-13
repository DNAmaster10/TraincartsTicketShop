package com.dnamaster10.traincartsticketshop.objects.buttons;

import com.dnamaster10.traincartsticketshop.util.Utilities;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import static com.dnamaster10.traincartsticketshop.objects.buttons.DataKeys.BUTTON_TYPE;

/**
 * A button which onl needs to hold basic information.
 * Used for buttons such as the search gui button, which does not need to store any additional data.
 * @see com.dnamaster10.traincartsticketshop.objects.buttons.DataKeys
 */
public class SimpleItemButton extends Button {
    //For simple buttons using an item
    private final String buttonType;
    private final Material material;
    private final Component displayName;

    @Override
    public ItemStack getItemStack() {
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;

        meta.displayName(displayName);
        meta.getPersistentDataContainer().set(BUTTON_TYPE, PersistentDataType.STRING, this.buttonType);
        item.setItemMeta(meta);

        return item;
    }

    /**
     * @param buttonType The button type
     * @param material The material to use
     * @param displayName The coloured display name for the ItemStack
     */
    public SimpleItemButton(String buttonType, Material material, Component displayName) {
        this.buttonType = buttonType;
        this.material = material;
        this.displayName = displayName;
    }

    /**
     * @param buttonType The button type
     * @param material The material to use
     * @param displayText The display text
     */
    public SimpleItemButton(String buttonType, Material material, String displayText) {
        this(buttonType, material, Utilities.parseColour(displayText));
    }
}
