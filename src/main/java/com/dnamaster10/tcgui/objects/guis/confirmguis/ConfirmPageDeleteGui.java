package com.dnamaster10.tcgui.objects.guis.confirmguis;

import com.dnamaster10.tcgui.objects.buttons.HeadData;
import com.dnamaster10.tcgui.objects.guis.PageBuilder;
import com.dnamaster10.tcgui.util.database.GuiAccessor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.sql.SQLException;

import static com.dnamaster10.tcgui.TraincartsGui.getPlugin;

public class ConfirmPageDeleteGui extends ConfirmActionGui {
    private final int deleteGuiPage;
    @Override
    protected void generate() {
        PageBuilder builder = new PageBuilder();
        GuiBuilder builder = new GuiBuilder(getDisplayName());

        //Check if a back button is needed
        if (getPlugin().getGuiManager().checkLastGui(getPlayer())) {
            builder.addBackButton();
        }

        //Add buttons specific to this gui type
        SimpleButton deletePageButton = new SimpleButton("confirm_action", HeadData.HeadType.RED_CROSS, "Delete Page");
        builder.addSimpleButton(deletePageButton, 22);

        setInventory(builder.getInventory());
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
