package com.dnamaster10.traincartsticketshop.objects.guis;

import com.dnamaster10.traincartsticketshop.objects.buttons.SimpleHeadButton;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.inventory.ItemStack;


import static com.dnamaster10.traincartsticketshop.util.ButtonUtils.getButtonType;
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
    public void handleClick(InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();
        String buttonType = getButtonType(clickedItem);
        if (buttonType == null) return;
        removeCursorItem();
        if (buttonType.equals("error")) {
            getPlayer().closeInventory();
        }
    }
    public ErrorGui(String errorMessage, Player p) {
        this.errorText = errorMessage;
        setPlayer(p);
    }
}
