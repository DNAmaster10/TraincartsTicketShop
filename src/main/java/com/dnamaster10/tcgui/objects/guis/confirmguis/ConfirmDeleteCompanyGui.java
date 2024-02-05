package com.dnamaster10.tcgui.objects.guis.confirmguis;

import com.dnamaster10.tcgui.objects.buttons.HeadData;
import com.dnamaster10.tcgui.objects.buttons.SimpleButton;
import com.dnamaster10.tcgui.objects.guis.GuiBuilder;
import com.dnamaster10.tcgui.util.database.CompanyAccessor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.sql.SQLException;

import static com.dnamaster10.tcgui.TraincartsGui.getPlugin;

public class ConfirmDeleteCompanyGui extends ConfirmActionGui {
    private String deleteCompany;
    private int companyId;
    @Override
    protected void generate() {
        GuiBuilder builder = new GuiBuilder(getDisplayName());

        //Check if back button is needed
        if (getPlugin().getGuiManager().checkLastGui(getPlayer())) {
            builder.addBackButton();
        }

        //Add buttons specific to this gui type
        //SimpleButton cancelButton = new SimpleButton("cancel")

        SimpleButton deleteCompanyButton = new SimpleButton("confirm_action", HeadData.HeadType.RED_CROSS, "Delete Company");
        builder.addSimpleButton(deleteCompanyButton, 23);

        SimpleButton cancelButton = new SimpleButton("cancel", HeadData.HeadType.GRAY_BACK_ARROW, "Cancel");
        builder.addSimpleButton(cancelButton, 21);

        setInventory(builder.getInventory());
    }

    @Override
    protected void confirmAction() {
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            try {
                CompanyAccessor companyAccessor = new CompanyAccessor();
                companyAccessor.deleteCompanyById(companyId);
            } catch (SQLException e) {
                getPlugin().reportSqlError(getPlayer(), e);
            }
            //Close this gui and return message
            getPlayer().sendMessage(ChatColor.GREEN + "Company \"" + getGuiName() + "\" was deleted");
            getPlayer().closeInventory();
        });
    }

    public ConfirmDeleteCompanyGui(String deleteCompany, Player p) {
        this.deleteCompany = deleteCompany;
        setPlayer(p);

        //Use ID in case someone renames the company while player is deleting it
        try {
            CompanyAccessor companyAccessor = new CompanyAccessor();
            companyId = companyAccessor.getCompanyIdByName(deleteCompany);
        } catch (SQLException e) {
            getPlugin().reportSqlError(p, e);
        }
    }
}
