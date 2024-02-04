package com.dnamaster10.tcgui.objects.guis.confirmguis;

import com.dnamaster10.tcgui.objects.guis.Gui;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.List;

import static com.dnamaster10.tcgui.TraincartsGui.getPlugin;

public abstract class ConfirmActionGui extends Gui {
    @Override
    public void open() {
        //There isn't an instance where a confirm action gui will need to access the database so this can be run synchronous
        generate();
        Bukkit.getScheduler().runTask(getPlugin(), () -> getPlayer().openInventory(getInventory()));
    }
    //Override generate gui so that it no longer throws an SQL exception
    @Override
    protected abstract void generate();

    @Override
    public void handleClick(InventoryClickEvent event, List<ItemStack> items) {
        for (ItemStack item : items) {
            String buttonType = getButtonType(item);
            switch (buttonType) {
                case "back" -> {
                    back();
                    return;
                }
                case "cancel" -> {
                    getPlayer().closeInventory();
                    return;
                }
                case "confirm_action" -> {
                    confirmAction();
                    return;
                }
            }
        }
    }

    protected abstract void confirmAction();
}
