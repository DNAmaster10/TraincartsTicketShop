package com.dnamaster10.tcgui;

import com.dnamaster10.tcgui.objects.Gui;
import com.dnamaster10.tcgui.util.Guis;
import com.dnamaster10.tcgui.util.Tickets;
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
    static boolean checkGuiCreateCommand(CommandSender sender, String[] args) {
        //Check config
        if (!TraincartsGui.plugin.getConfig().getBoolean("AllowGuiCreate")) {
            returnError(sender, "Gui creation is disabled on this server");
            return false;
        }

        //Check syntax
        if (args.length < 3) {
            returnError(sender, "Please enter a valid GUI name");
            return false;
        }
        if (args.length > 3) {
            returnError(sender, "Invalid sub-command \"" + args[3] + "\"");
            return false;
        }
        if (!checkString(args[2])) {
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
    static boolean checkTicketCreateCommand(CommandSender sender, String[] args) {
        //Example: /tcgui ticket create <traincart_name> <display_name>
        //Check config
        if (!TraincartsGui.plugin.getConfig().getBoolean("AllowTicketCreate")) {
            returnError(sender, "Ticket creation is disabled on this server");
            return false;
        }

        //Check syntax
        if (args.length < 4) {
            returnError(sender, "Missing argument(s): /tcgui ticket create <tc_ticket_name> <display_name>");
            return false;
        }
        if (args.length > 4) {
            returnError(sender, "Invalid sub-command \"" + args[4] + "\": /tcgui ticket create <tc_ticket_name> <display_name>");
            return false;
        }

        //Check permissions and that sender is a player
        if (sender instanceof Player p) {
            if (!p.hasPermission("tcgui.createticket")) {
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
            case "gui" -> {
                if (args.length < 2) {
                    returnError(sender, "Please enter a valid sub-command: create/edit");
                }
                switch(args[1]) {
                    case "create" -> {
                        if (checkGuiCreateCommand(sender, args)) {
                            Guis.createGuiCommand((Player) sender, args[2]);
                        }
                    }
                    default -> {
                        returnError(sender, "Please enter a valid sub-command: create/edit");
                    }
                }
            }
            case "ticket" -> {
                if (args.length < 2) {
                    returnError(sender, "Please enter a valid sub-command: create");
                }
                switch (args[1]) {
                    case "create" -> {
                        if (checkTicketCreateCommand(sender, args)) {
                            Tickets.createTicketCommand((Player) sender, args[2], args[3]);
                        }
                    }
                    default -> {
                        returnError(sender, "Please enter a valid sub-command: create");
                    }
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
