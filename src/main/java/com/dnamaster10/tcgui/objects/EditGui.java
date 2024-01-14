package com.dnamaster10.tcgui.objects;

import com.dnamaster10.tcgui.TraincartsGui;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class EditGui extends Gui {
    @Override
    public void open(Player p) {
        //Method must be run synchronous
        if (Bukkit.isPrimaryThread()) {
            p.openInventory(getInventory());
            return;
        }
        //Else, run synchronous
        Bukkit.getScheduler().runTask(TraincartsGui.plugin, () -> {
            p.openInventory(getInventory());
        });
    }

    @Override
    public void nextPage(Player p) {

    }

    @Override
    public void prevPage(Player p) {

    }

    public EditGui(String guiName) {
        //Should be called from an asynchronous thread
        setPage(0);
        setInventory(Bukkit.createInventory(null, 54));
    }
}
