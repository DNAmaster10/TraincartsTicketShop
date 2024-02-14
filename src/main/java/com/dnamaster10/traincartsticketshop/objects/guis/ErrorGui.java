package com.dnamaster10.traincartsticketshop.objects.guis;

import com.dnamaster10.traincartsticketshop.objects.buttons.HeadData;
import com.dnamaster10.traincartsticketshop.objects.buttons.SimpleHeadButton;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

import static com.dnamaster10.traincartsticketshop.TraincartsTicketShop.getPlugin;
import static com.dnamaster10.traincartsticketshop.objects.buttons.Buttons.getButtonType;
import static com.dnamaster10.traincartsticketshop.objects.buttons.HeadData.HeadType.RED_CROSS;

public class ErrorGui extends Gui {
    //Used when an error occurs building a gui (Such as if the gui was deleted while a player was using it)
    private final String errorText;
    @Override
    public void open() {
        generate();
        getPlayer().openInventory(getInventory());
    }
    protected void generate() {
        PageBuilder pageBuilder = new PageBuilder();

        SimpleHeadButton errorButton = new SimpleHeadButton("error", RED_CROSS, errorText);
        pageBuilder.addButton(22, errorButton);

        setInventory(new InventoryBuilder(pageBuilder.getPage(), "Error").getInventory());
    }

    @Override
    public void handleClick(InventoryClickEvent event, List<ItemStack> items) {
        for (ItemStack item : items) {
            String buttonType = getButtonType(item);
            if (buttonType == null) {
                continue;
            }
            getPlayer().setItemOnCursor(null);
            switch (buttonType) {
                case "error" -> {
                    getPlayer().closeInventory();
                    return;
                }
            }
        }
    }
    public ErrorGui(String errorMessage, Player p) {
        this.errorText = errorMessage;
        setPlayer(p);
    }
}
