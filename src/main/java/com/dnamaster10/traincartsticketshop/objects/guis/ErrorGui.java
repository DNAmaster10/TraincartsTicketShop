package com.dnamaster10.traincartsticketshop.objects.guis;

import com.dnamaster10.traincartsticketshop.objects.buttons.SimpleHeadButton;
import com.dnamaster10.traincartsticketshop.objects.guis.interfaces.ClickHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import static com.dnamaster10.traincartsticketshop.TraincartsTicketShop.getPlugin;
import static com.dnamaster10.traincartsticketshop.objects.buttons.HeadData.HeadType.RED_CROSS;
import static com.dnamaster10.traincartsticketshop.util.ButtonUtils.getButtonType;

public class ErrorGui extends Gui implements InventoryHolder, ClickHandler {
    //TODO may be possible to completely remove this GUI type
    private final Inventory inventory;
    private final Player player;

    public ErrorGui(Player player, String errorMessage) {
        this.player = player;

        Page page = new Page();
        page.setDisplayName("Error");

        SimpleHeadButton errorButton = new SimpleHeadButton("error", RED_CROSS, errorMessage);
        page.addButton(22, errorButton);

        inventory = page.getAsInventory(this);
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();
        String buttonType = getButtonType(clickedItem);
        if (buttonType == null) return;
        player.setItemOnCursor(null);
        if (buttonType.equals("error")) {
            Bukkit.getScheduler().runTaskLater(getPlugin(), player::closeInventory, 1L);
        }
    }

    @Override
    public void open() {
        Bukkit.getScheduler().runTask(getPlugin(), () -> player.openInventory(inventory));
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
