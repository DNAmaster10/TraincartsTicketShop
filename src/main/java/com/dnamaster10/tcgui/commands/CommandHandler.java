package com.dnamaster10.tcgui.commands;

import com.dnamaster10.tcgui.TraincartsGui;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public abstract class CommandHandler {
    //For synchronous command checks (E.g. syntax checks)
    abstract boolean checkSync(CommandSender sender, String[] args);
    //For asynchronous command checks (E.g. database checks)
    abstract boolean checkAsync(CommandSender sender, String[] args);
    //Runs the command after all checks are completed
    abstract void execute(CommandSender sender, String[] args);
    //Runs appropriate checks before command is executed
    public abstract void handle(CommandSender sender, String[] args);
    public TraincartsGui getPlugin() {
        return TraincartsGui.plugin;
    }
    public void returnError(CommandSender sender, String error) {
        if (sender instanceof Player p) {
            p.sendMessage(ChatColor.RED + error);
        }
        else if (sender instanceof ConsoleCommandSender) {
            getPlugin().getLogger().warning(error);
        }
    }
}
