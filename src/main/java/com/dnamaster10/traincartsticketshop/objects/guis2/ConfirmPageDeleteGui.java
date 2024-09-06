package com.dnamaster10.traincartsticketshop.objects.guis2;

import com.dnamaster10.traincartsticketshop.objects.buttons.HeadData;
import com.dnamaster10.traincartsticketshop.objects.buttons.SimpleHeadButton;
import com.dnamaster10.traincartsticketshop.objects.guis2.interfaces.Clickable;
import com.dnamaster10.traincartsticketshop.objects.guis2.interfaces.Openable;
import com.dnamaster10.traincartsticketshop.util.Session;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import static com.dnamaster10.traincartsticketshop.TraincartsTicketShop.getPlugin;
import static com.dnamaster10.traincartsticketshop.util.ButtonUtils.getButtonType;

public class ConfirmPageDeleteGui implements InventoryHolder, Clickable, Openable {
    //Gui used for when someone wants to delete a page within a gui
    private final Player player;
    private final int guiId;
    private final int pageNumber;
    private final Inventory inventory;

    public ConfirmPageDeleteGui(Player player, int guiId, int pageNumber) {
        this.player = player;
        this.guiId = guiId;
        this.pageNumber = pageNumber;
        Page page = new Page();

        page.setDisplayName(ChatColor.RED + "Confirm Page Deletion");
        if (getPlugin().getGuiManager().getSession(player).checkBack()) {
            page.addBackButton();
        }

        SimpleHeadButton deletePageButton = new SimpleHeadButton("confirm_action", HeadData.HeadType.RED_CROSS, "Delete Page");
        page.addButton(22, deletePageButton);

        inventory = page.getAsInventory(this);
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();
        String buttonType = getButtonType(clickedItem);
        if (buttonType == null) return;
        player.setItemOnCursor(null);

        switch (buttonType) {
            case "back" -> {
                Session session = getPlugin().getGuiManager().getSession(player);
                if (!session.checkBack()) return;
                session.back();
            }
            case "confirm_action" -> {
                Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
                    //try {
                        
                    //}
                });
            }
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
