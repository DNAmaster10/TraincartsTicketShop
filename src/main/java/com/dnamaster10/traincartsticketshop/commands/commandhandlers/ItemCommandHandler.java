package com.dnamaster10.traincartsticketshop.commands.commandhandlers;

import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import static com.dnamaster10.traincartsticketshop.objects.buttons.DataKeys.BUTTON_TYPE;

public abstract class ItemCommandHandler extends CommandHandler {
    //For commands which alter the players inventory
    protected void returnWrongItemError(CommandSender sender, String correctItem) {
        returnError(sender, "You must be holding a " + correctItem + " in your main hand");
    }
    protected String getButtonType(ItemStack item) {
        if (item == null) {
            return null;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return null;
        }
        PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
        if (!dataContainer.has(BUTTON_TYPE, PersistentDataType.STRING)) {
            return null;
        }
        return dataContainer.get(BUTTON_TYPE, PersistentDataType.STRING);
    }
}
