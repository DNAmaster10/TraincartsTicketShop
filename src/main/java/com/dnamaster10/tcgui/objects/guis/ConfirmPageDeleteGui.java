package com.dnamaster10.tcgui.objects.guis;

import com.dnamaster10.tcgui.objects.buttons.DeletePageButton;
import com.dnamaster10.tcgui.util.database.GuiAccessor;
import com.dnamaster10.tcgui.util.gui.GuiBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.List;

public class ConfirmPageDeleteGui extends Gui {
    private int deleteGuiPage;
    @Override
    public void open() {
        try {
            generate();
        } catch (SQLException e) {
            removeCursorItemAndClose();
            getPlugin().reportSqlError(getPlayer(), e);
            return;
        }
        Bukkit.getScheduler().runTask(getPlugin(), () -> getPlayer().openInventory(getInventory()));
    }

    @Override
    protected void generate() throws SQLException {
        GuiBuilder builder = new GuiBuilder(getDisplayName());
        builder.addBackButton();
        DeletePageButton button = new DeletePageButton();
        builder.addItem(13, button.getItemStack());

        setInventory(builder.getInventory());
    }
    @Override
    public void handleClick(InventoryClickEvent event, List<ItemStack> items) {
        for (ItemStack item : items) {
            String buttonType = getButtonType(item);
            if (buttonType == null) {
                continue;
            }
            switch (buttonType) {
                case "back" -> {
                    back();
                    return;
                }
                case "delete_page" -> {
                    deletePage();
                    return;
                }
            }
        }
    }
    private void back() {
        removeCursorItem();
        if (!getPlugin().getGuiManager().checkLastGui(getPlayer())) {
            return;
        }
        getPlugin().getGuiManager().back(getPlayer());
    }
    private void deletePage() {
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            try {
                GuiAccessor guiAccessor = new GuiAccessor();
                guiAccessor.deletePage(getGuiId(), deleteGuiPage);
            } catch (SQLException e) {
                removeCursorItemAndClose();
                getPlugin().reportSqlError(getPlayer(), e);
            }
            //Go back to the previous gui
            removeCursorItem();
            getPlugin().getGuiManager().back(getPlayer());
        });
    }
    public ConfirmPageDeleteGui(int deleteGuiId, int deletePageNumber, Player p) {
        setPlayer(p);
        setGuiId(deleteGuiId);
        this.deleteGuiPage = deletePageNumber;
        setDisplayName(ChatColor.RED + "Confirm Page Deletion");
    }
}
