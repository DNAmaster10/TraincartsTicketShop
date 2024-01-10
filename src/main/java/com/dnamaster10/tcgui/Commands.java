package com.dnamaster10.tcgui;

import com.dnamaster10.tcgui.util.Guis;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.regex.Pattern;

public class Commands {
    private static final Pattern STRING_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]+$");
    static void returnError(CommandSender sender, String error) {
        //Returns an error to sender
        if (sender instanceof Player p) {
            p.sendMessage(ChatColor.RED + error);
        }
        else if (sender instanceof ConsoleCommandSender) {
            TraincartsGui.plugin.getLogger().warning(error);
        }
    }
    static boolean checkString(String value) {
        //Checks that a string only contains underscores, dashes, numbers and letters
        return STRING_PATTERN.matcher(value).matches();
    }
    static boolean checkCreateCommand(CommandSender sender, String[] args) {
        //Check config
        if (!TraincartsGui.plugin.getConfig().getBoolean("AllowGuiCreate")) {
            returnError(sender, "Gui creation is disabled on this server");
            return false;
        }

        //Check syntax
        if (args.length < 2) {
            returnError(sender, "Please enter a valid GUI name");
            return false;
        }
        if (args.length > 2) {
            returnError(sender, "Invalid sub-command \"" + args[2] + "\"");
            return false;
        }
        if (!checkString(args[1])) {
            returnError (sender, "Gui names can only contain characters a/A - z/Z, numbers, underscores and dashes");
            return false;
        }

        //Check permissions and that sender is a player
        if (sender instanceof Player p) {
            if (!p.hasPermission("tcgui.creategui")) {
                returnError(sender, "You do not have permission to perform that action");
                return false;
            }
        }
        else {
            returnError(sender, "Command must be executed by a player");
            return false;
        }
        return true;
    }
    public static void execute(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("tcgui")) {
            //If it isn't a tcgui command return
            return;
        }
        if (args.length < 1) {
            returnError(sender, "Please enter a valid sub-command");
            return;
        }
        switch(args[0]) {
            case "create" -> {
                //Check syntax and permissions before database checks
                if (checkCreateCommand(sender, args)) {
                    Guis.createGuiCommand((Player) sender, args[1]);
                }
            }
            default -> {
                returnError(sender, "Unrecognised command \"" + args[0] + "\"");
                return ;
            }
        }
        //Takes in a command and checks whether it is okay to be sent.
        //Checks syntax, checks permissions etc
    }
}
