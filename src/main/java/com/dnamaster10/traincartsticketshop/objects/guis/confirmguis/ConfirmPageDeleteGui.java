package com.dnamaster10.traincartsticketshop.objects.guis.confirmguis;

import com.dnamaster10.traincartsticketshop.objects.buttons.HeadData;
import com.dnamaster10.traincartsticketshop.objects.buttons.SimpleHeadButton;
import com.dnamaster10.traincartsticketshop.objects.guis.InventoryBuilder;
import com.dnamaster10.traincartsticketshop.objects.guis.PageBuilder;
import com.dnamaster10.traincartsticketshop.util.database.GuiAccessor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.sql.SQLException;

import static com.dnamaster10.traincartsticketshop.TraincartsTicketShop.getPlugin;
import static com.dnamaster10.traincartsticketshop.objects.buttons.HeadData.HeadType.RED_CROSS;

public class ConfirmPageDeleteGui extends ConfirmActionGui {
    private final int deleteGuiPage;
    @Override
    protected void generate() {
        PageBuilder builder = new PageBuilder();

        //Check if back button is needed
        if (getSession().checkBack()) {
            builder.addBackButton();
        }

        //Add buttons specific to this gui type
        SimpleHeadButton deletePageButton = new SimpleHeadButton("confirm_action", RED_CROSS, "Delete Page");
        builder.addButton(22, deletePageButton);

        setInventory(new InventoryBuilder(builder.getPage(), getDisplayName()).getInventory());
    }
    @Override
    protected void confirmAction() {
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
            getSession().back();
        });
    }
    public ConfirmPageDeleteGui(int deleteGuiId, int deletePageNumber, Player p) {
        setPlayer(p);
        setGuiId(deleteGuiId);
        this.deleteGuiPage = deletePageNumber;
        setDisplayName(ChatColor.RED + "Confirm Page Deletion");
    }
}
