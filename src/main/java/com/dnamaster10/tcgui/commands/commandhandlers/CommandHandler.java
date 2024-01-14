package com.dnamaster10.tcgui.commands.commandhandlers;

import com.dnamaster10.tcgui.TraincartsGui;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.regex.Pattern;

public abstract class CommandHandler<E extends Exception> {
    //Extends exception is used for the checkAsync method and execute method
    //which may throw an SQL exception if accessing the database.
    private static final Pattern STRING_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]+$");
    //For synchronous command checks (E.g. syntax checks)
    abstract boolean checkSync(CommandSender sender, String[] args);
    //For asynchronous command checks (E.g. database checks)
    abstract boolean checkAsync(CommandSender sender, String[] args) throws E;
    //Runs the command after all checks are completed
    abstract void execute(CommandSender sender, String[] args) throws E;
    //Runs appropriate checks before command is executed
    public abstract void handle(CommandSender sender, String[] args);
    public TraincartsGui getPlugin() {
        return TraincartsGui.plugin;
    }
    public void returnError(CommandSender sender, String error) {
        //Returns an error to sender
        if (sender instanceof Player p) {
            p.sendMessage(ChatColor.RED + error);
        }
        else if (sender instanceof ConsoleCommandSender) {
            getPlugin().getLogger().warning(error);
        }
    }
    public boolean checkStringFormat(String input) {
        //Checks that a string only contains letters, numbers, underscores and dashes
        return STRING_PATTERN.matcher(input).matches();
    }
}
