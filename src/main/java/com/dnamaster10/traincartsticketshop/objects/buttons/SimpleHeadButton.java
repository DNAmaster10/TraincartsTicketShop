package com.dnamaster10.traincartsticketshop.objects.buttons;

import com.dnamaster10.traincartsticketshop.util.Utilities;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;
import com.dnamaster10.traincartsticketshop.objects.buttons.HeadData.HeadType;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import static com.dnamaster10.traincartsticketshop.objects.buttons.DataKeys.BUTTON_TYPE;

/**
 * A head button which only holds basic information.
 * Used for buttons such as the insert page button, which does not need to store any additional data.
 * @see com.dnamaster10.traincartsticketshop.objects.buttons.DataKeys
 * @see com.dnamaster10.traincartsticketshop.objects.buttons.HeadData
 */
public class SimpleHeadButton extends Button {
    //For simple buttons using a head
    private final String buttonType;
    private final HeadType headType;
    private final Component displayName;

    @Override
    public ItemStack getItemStack() {
        ItemStack item = HeadData.getPlayerHeadItem(headType);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;

        meta.displayName(displayName);
        meta.getPersistentDataContainer().set(BUTTON_TYPE, PersistentDataType.STRING, buttonType);
        item.setItemMeta(meta);

        return item;
    }

    /**
     * @param buttonType The button type
     * @param headType The head type
     * @param displayName The display name for the button
     */
    public SimpleHeadButton(String buttonType, HeadType headType, Component displayName) {
        this.buttonType = buttonType;
        this.headType = headType;
        this.displayName = displayName;
    }

    /**
     * @param buttonType The button type
     * @param headType The head type
     * @param displayName The display text for the button
     */
    public SimpleHeadButton(String buttonType, HeadType headType, String displayName) {
        this(buttonType, headType, Utilities.parseColour(displayName));
    }
}
