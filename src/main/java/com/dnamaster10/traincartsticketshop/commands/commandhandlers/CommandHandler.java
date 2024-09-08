package com.dnamaster10.traincartsticketshop.commands.commandhandlers;

import com.dnamaster10.traincartsticketshop.util.Utilities;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import static com.dnamaster10.traincartsticketshop.TraincartsTicketShop.getPlugin;

public abstract class CommandHandler {
    //Extends exception is used for the checkAsync method and execute method
    //which may throw an SQL exception if accessing the database.

    //For synchronous command checks (E.g. syntax checks)
    protected abstract boolean checkSync(CommandSender sender, String[] args);

    //Runs appropriate checks before command is executed
    public abstract void handle(CommandSender sender, String[] args);

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
        returnError(sender, "No gui with the ID \"" + guiName + "\" exists");
    }

    protected void returnWrongItemError(CommandSender sender, String correctItem) {
        sender.sendMessage(ChatColor.RED + "You must be holding a " + correctItem + " to perform that action");
    }

    protected void returnOnlyPlayersExecuteError(CommandSender sender) {
        sender.sendMessage(ChatColor.RED + "Command must be executed by a player");
    }

    protected void returnInsufficientPermissionsError(CommandSender sender) {
        sender.sendMessage(ChatColor.RED + "You do not have permission to perform that action");
    }

    protected void returnMissingArgumentsError(CommandSender sender, String commandSyntax) {
        sender.sendMessage(ChatColor.RED + "Missing argument(s): " + commandSyntax);
    }

    protected void returnInvalidSubCommandError(CommandSender sender, String subCommand) {
        sender.sendMessage(ChatColor.RED + "Invalid sub-command \"" + subCommand + "\"");
    }

    protected boolean checkGuiNameSyntax(String input) {
        return input.length() > 3 && input.length() < 20 && !Utilities.checkSpecialCharacters(input);
    }
}
