package com.dnamaster10.tcgui;

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
    static boolean checkCreateCommand(CommandSender sender, String args[]) {
        //Check syntax
        if (args.length < 2) {
            returnError(sender, "Please enter a valid GUI name");
            return false;
        }
        if (args.length > 2) {
            returnError(sender, "Invalid sub-command \"" + args[2] + "\"");
            return false;
        }

    }
    public static boolean checkCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("tcgui")) {
            //If it isn't a tcgui command return
            return true;
        }
        if (args.length < 1) {
            returnError(sender, "Please enter a valid sub-command");
            return false;
        }
        switch(args[0]) {
            case "create" -> {
                //Check that a GUI name was entered
                return checkCreateCommand(sender, args);
            }
            default -> {
                returnError(sender, "Unrecognised command \"" + args[0] + "\"");
                return false;
            }
        }
        //Takes in a command and checks whether it is okay to be sent.
        //Checks syntax, checks permissions etc
    }
}
