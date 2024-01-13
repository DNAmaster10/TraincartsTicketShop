package com.dnamaster10.tcgui.commands;

import com.dnamaster10.tcgui.TraincartsGui;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class CommandDispatcher {
    private static void returnError(CommandSender sender, String error) {
        if (sender instanceof Player p) {
            p.sendMessage(ChatColor.RED + error);
        }
        else if (sender instanceof ConsoleCommandSender) {
            TraincartsGui.plugin.getLogger().warning(error);
        }
    }
    public static void dispatchCommand(CommandSender sender, Command command, String[] args) {
        //Check that command is a tcgui command. If not do nothing
        if (!command.getName().equalsIgnoreCase("tcgui")) {
            return;
        }

        //Check that a sub-command was provided
        if (args.length < 1) {
            returnError(sender, "Please enter a valid sub-command");
        }

        //Decide how to handle command
        switch (args[0]) {
            case "gui" -> {
                switch (args[1]) {
                    case "create" -> {
                        GuiCreateCommandHandler handler = new GuiCreateCommandHandler();
                        handler.handle(sender, args);
                    }
                    default -> {
                        returnError(sender, "Unrecognised sub-command \"" + args[1] + "\"");
                    }
                }
            }
            case "ticket" -> {
                switch (args[1]) {
                    case "create" -> {
                        TicketCreateCommandHandler handler = new TicketCreateCommandHandler();
                        handler.handle(sender, args);
                    }
                    default -> {
                        returnError(sender, "Unrecognised sub-command \"" + args[1] + "\"");
                    }
                }
            }
            default -> {
                returnError(sender, "Unrecognised command \"" + args[0] + "\"");
            }
        }
    }
}