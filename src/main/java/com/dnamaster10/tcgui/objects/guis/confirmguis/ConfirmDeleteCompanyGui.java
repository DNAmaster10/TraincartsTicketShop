package com.dnamaster10.tcgui.objects.guis.confirmguis;

import com.dnamaster10.tcgui.objects.buttons.HeadData;
import com.dnamaster10.tcgui.objects.buttons.SimpleButton;
import com.dnamaster10.tcgui.objects.guis.GuiBuilder;
import com.dnamaster10.tcgui.util.database.GuiAccessor;
import org.bukkit.Bukkit;
import org.bukkit.Material;

import java.sql.SQLException;

import static com.dnamaster10.tcgui.TraincartsGui.getPlugin;

public class ConfirmDeleteCompanyGui extends ConfirmActionGui {
    private final String deleteCompany;

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
                GuiAccessor guiAccessor = new GuiAccessor();


            } catch (SQLException e) {

            }
        });
    }

    public ConfirmDeleteCompanyGui(String deleteCompany) {
        this.deleteCompany = deleteCompany;
    }
}
