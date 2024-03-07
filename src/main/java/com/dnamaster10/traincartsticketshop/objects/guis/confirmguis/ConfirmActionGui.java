package com.dnamaster10.traincartsticketshop.objects.guis.confirmguis;

import com.dnamaster10.traincartsticketshop.objects.guis.Gui;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import static com.dnamaster10.traincartsticketshop.TraincartsTicketShop.getPlugin;
import static com.dnamaster10.traincartsticketshop.util.ButtonUtils.getButtonType;

public abstract class ConfirmActionGui extends Gui {
    @Override
    public void open() {
        //There isn't an instance where a confirm action gui will need to access the database so this can be run synchronous
        generate();
        Bukkit.getScheduler().runTask(getPlugin(), () -> getPlayer().openInventory(getInventory()));
    }
    //Override generate gui so that it no longer throws an SQL exception
    protected abstract void generate();

    @Override
    public void handleClick(InventoryClickEvent event, ItemStack clickedItem) {
        String buttonType = getButtonType(clickedItem);
        if (buttonType == null) {
            return;
        }
        //Remove item from cursor since it is a button
        removeCursorItem();
        switch (buttonType) {
            case "back" -> back();
            case "cancel" -> getPlayer().closeInventory();
            case "confirm_action" -> confirmAction();
        }
    }

    protected abstract void confirmAction();
}
