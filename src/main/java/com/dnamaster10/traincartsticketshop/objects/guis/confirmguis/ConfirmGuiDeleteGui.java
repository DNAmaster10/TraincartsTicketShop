package com.dnamaster10.traincartsticketshop.objects.guis.confirmguis;

import com.dnamaster10.traincartsticketshop.objects.buttons.HeadData;
import com.dnamaster10.traincartsticketshop.objects.buttons.SimpleHeadButton;
import com.dnamaster10.traincartsticketshop.objects.guis.InventoryBuilder;
import com.dnamaster10.traincartsticketshop.objects.guis.PageBuilder;
import com.dnamaster10.traincartsticketshop.util.database.GuiAccessor;
import com.dnamaster10.traincartsticketshop.util.exceptions.DMLException;
import com.dnamaster10.traincartsticketshop.util.exceptions.DQLException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import static com.dnamaster10.traincartsticketshop.TraincartsTicketShop.getPlugin;

public class ConfirmGuiDeleteGui extends ConfirmActionGui {
    private final int deleteGuiId;

    @Override
    protected void generate() {
        PageBuilder builder = new PageBuilder();

        if (getSession().checkBack()) {
            builder.addBackButton();
        }

        SimpleHeadButton deleteGuiButton = new SimpleHeadButton("confirm_action", HeadData.HeadType.RED_CROSS, "Delete Gui");
        builder.addButton(22, deleteGuiButton);

        setInventory(new InventoryBuilder(builder.getPage(), getDisplayName()).getInventory());
    }
    @Override
    protected void confirmAction() {
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            try {
                GuiAccessor guiAccessor = new GuiAccessor();
                guiAccessor.deleteGuiById(deleteGuiId);
            } catch (DQLException | DMLException e) {
                getPlugin().handleSqlException(getPlayer(), e);
            }
            closeInventory();
        });
    }
    public ConfirmGuiDeleteGui(int deleteGuiId, Player p) {
        setPlayer(p);
        this.deleteGuiId = deleteGuiId;
        setDisplayName(ChatColor.RED + "Confirm Gui Deletion");
    }
}
