package com.dnamaster10.tcgui.commands;

import com.dnamaster10.tcgui.Commands;
import com.dnamaster10.tcgui.TraincartsGui;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class GuiCreateCommandHandler extends CommandHandler {

    @Override
    boolean checkSync(CommandSender sender, String[] args) {
        //Synchronous checks (Syntax etc.)
        //Check config
        if (!getPlugin().getConfig().getBoolean("AllowGuiCreate")) {

        }
        return false;
    }

    @Override
    boolean checkAsync(CommandSender sender, String[] args) {
        //Asynchronous checks (Database etc.)
        return false;
    }

    @Override
    void execute(CommandSender sender, String[] args) {
        //Runs the command
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        if (!checkSync(sender, args)) {
            return;
        }
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            if (!checkAsync(sender, args)) {
                return;
            }
            execute(sender, args);
        });
    }
}
