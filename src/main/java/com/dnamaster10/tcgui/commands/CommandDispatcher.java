package com.dnamaster10.tcgui.commands;

import com.dnamaster10.tcgui.TraincartsGui;
import com.dnamaster10.tcgui.commands.commandhandlers.*;
import com.dnamaster10.tcgui.commands.commandhandlers.gui.GuiCreateCommandHandler;
import com.dnamaster10.tcgui.commands.commandhandlers.gui.GuiEditCommandHandler;
import com.dnamaster10.tcgui.commands.commandhandlers.gui.GuiRenameCommandHandler;
import com.dnamaster10.tcgui.commands.commandhandlers.gui.GuiShopOpenCommandHandler;
import com.dnamaster10.tcgui.commands.commandhandlers.ticket.TicketCreateCommandHandler;
import com.dnamaster10.tcgui.commands.commandhandlers.ticket.TicketSetDisplayNameCommandHandler;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CommandDispatcher implements CommandExecutor {
    private static void returnError(CommandSender sender, String error) {
        if (sender instanceof Player p) {
            p.sendMessage(ChatColor.RED + error);
        }
        else if (sender instanceof ConsoleCommandSender) {
            TraincartsGui.plugin.getLogger().warning(error);
        }
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        //Check that a sub-command was provided
        if (args.length < 1) {
            returnError(sender, "Please enter a valid sub-command");
            return true;
        }

        //Decide how to handle command
        switch (args[0]) {
            case "gui" -> {
                switch (args[1]) {
                    case "create" -> {
                        GuiCreateCommandHandler handler = new GuiCreateCommandHandler();
                        handler.handle(sender, args);
                    }
                    case "rename" -> {
                        GuiRenameCommandHandler handler = new GuiRenameCommandHandler();
                        handler.handle(sender, args);
                    }
                    case "edit" -> {
                        GuiEditCommandHandler handler = new GuiEditCommandHandler();
                        handler.handle(sender, args);
                    }
                    default -> returnError(sender, "Unrecognised sub-command \"" + args[1] + "\"");
                }
            }
            case "ticket" -> {
                switch (args[1].toLowerCase()) {
                    case "create" -> {
                        TicketCreateCommandHandler handler = new TicketCreateCommandHandler();
                        handler.handle(sender, args);
                    }
                    case "setdisplayname" -> {
                        TicketSetDisplayNameCommandHandler handler = new TicketSetDisplayNameCommandHandler();
                        handler.handle(sender, args);
                    }
                    default -> returnError(sender, "Unrecognised sub-command \"" + args[1] + "\"");
                }
            }
            case "shop" -> {
                switch (args[1]) {
                    case "open" -> {
                        GuiShopOpenCommandHandler handler = new GuiShopOpenCommandHandler();
                        handler.handle(sender, args);
                    }
                    default -> returnError(sender, "Unrecognised sub-command \"" + args[1] + "\"");
                }
            }
            case "linker" -> {
                switch (args[1]) {
                    case "create" -> {
                        LinkerCreateCommandHandler handler = new LinkerCreateCommandHandler();
                        handler.handle(sender, args);
                    }
                    default -> returnError(sender, "Unrecognised sub-command \"" + args[1] + "\"");
                }
            }
            default -> returnError(sender, "Unrecognised command \"" + args[0] + "\"");
        }
        return true;
    }
}
