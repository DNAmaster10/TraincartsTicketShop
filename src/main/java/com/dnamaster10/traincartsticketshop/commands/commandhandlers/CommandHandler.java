package com.dnamaster10.traincartsticketshop.commands.commandhandlers;

import com.dnamaster10.traincartsticketshop.TraincartsTicketShop;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.regex.Pattern;

public abstract class CommandHandler {
    //TODO May need to also create a sync-command handler and an aync-command handler to eliminate needing to override the handle method
    //Extends exception is used for the checkAsync method and execute method
    //which may throw an SQL exception if accessing the database.
    private static final Pattern STRING_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]+$");
    //For synchronous command checks (E.g. syntax checks)
    protected abstract boolean checkSync(CommandSender sender, String[] args);
    //Runs appropriate checks before command is executed
    public abstract void handle(CommandSender sender, String[] args);
    public TraincartsTicketShop getPlugin() {
        return TraincartsTicketShop.getPlugin();
    }
    protected void returnError(CommandSender sender, String error) {
        //Returns an error to sender
        if (sender instanceof Player p) {
            p.sendMessage(ChatColor.RED + error);
        }
        else if (sender instanceof ConsoleCommandSender) {
            getPlugin().getLogger().warning(error);
        }
    }
    protected void returnGuiNotFoundError(CommandSender sender, String guiName) {
        //Returns a "No gui with name "x" exists error to the sender
        returnError(sender, "No gui with the name \"" + guiName + "\" exists");
    }
    protected void returnWrongItemError(CommandSender sender, String correctItem) {
        sender.sendMessage(ChatColor.RED + "You must be holding a " + correctItem + " to perform that action");
    }
    protected boolean checkStringFormat(String input) {
        //Checks that a string only contains letters, numbers, underscores and dashes
        return STRING_PATTERN.matcher(input).matches();
    }
    protected boolean checkGuiNameSyntax(String guiName) {
        return guiName.length() <= 25 && guiName.length() >= 3 && checkStringFormat(guiName);
    }
    protected boolean checkCompanyNameSyntax(String companyName) {
        return companyName.length() <= 25 && companyName.length() >= 3 && checkStringFormat(companyName);
    }
}
