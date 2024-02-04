package com.dnamaster10.tcgui.commands;

import com.dnamaster10.tcgui.commands.commandhandlers.company.CompanyCreateCommandHandler;
import com.dnamaster10.tcgui.commands.commandhandlers.editor.EditorRemoveAllCommandHandler;
import com.dnamaster10.tcgui.commands.commandhandlers.editor.EditorRemoveCommandHandler;
import com.dnamaster10.tcgui.commands.commandhandlers.gui.*;
import com.dnamaster10.tcgui.commands.commandhandlers.editor.EditorAddCommandHandler;
import com.dnamaster10.tcgui.commands.commandhandlers.editor.EditorListCommandHandler;
import com.dnamaster10.tcgui.commands.commandhandlers.linker.LinkerCreateCommandHandler;
import com.dnamaster10.tcgui.commands.commandhandlers.linker.LinkerSetDisplayNameCommandHandler;
import com.dnamaster10.tcgui.commands.commandhandlers.linker.LinkerSetDestinationPageCommandHandler;
import com.dnamaster10.tcgui.commands.commandhandlers.shop.ShopOpenCommandHandler;
import com.dnamaster10.tcgui.commands.commandhandlers.ticket.TicketCreateCommandHandler;
import com.dnamaster10.tcgui.commands.commandhandlers.ticket.TicketSetDisplayNameCommandHandler;
import com.dnamaster10.tcgui.commands.commandhandlers.ticket.TicketSetTraincartsTicket;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static com.dnamaster10.tcgui.TraincartsGui.getPlugin;

public class CommandDispatcher implements CommandExecutor {
    private static void returnError(CommandSender sender, String error) {
        if (sender instanceof Player p) {
            p.sendMessage(ChatColor.RED + error);
        }
        else if (sender instanceof ConsoleCommandSender) {
            getPlugin().getLogger().warning(error);
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
        switch (args[0].toLowerCase()) {
            case "company" -> {
                switch (args[1].toLowerCase()) {
                    case "create" -> {
                        CompanyCreateCommandHandler handler = new CompanyCreateCommandHandler();
                        handler.handle(sender, args);
                    }
                }
            }
            case "gui" -> {
                switch (args[1].toLowerCase()) {
                    case "create" -> {
                        GuiCreateCommandHandler handler = new GuiCreateCommandHandler();
                        handler.handle(sender, args);
                    }
                    case "delete" -> {
                        GuiDeleteCommandHandler handler = new GuiDeleteCommandHandler();
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
                    case "setdisplayname" -> {
                        GuiSetDisplayNameCommandHandler handler = new GuiSetDisplayNameCommandHandler();
                        handler.handle(sender, args);
                    }
                    case "searchlinkers" -> {
                        GuiSearchLinkersCommandHandler handler = new GuiSearchLinkersCommandHandler();
                        handler.handle(sender, args);
                    }
                    case "searchtickets" -> {
                        GuiSearchTicketsCommandHandler handler = new GuiSearchTicketsCommandHandler();
                        handler.handle(sender, args);
                    }
                    case "transfer" -> {
                        GuiTransferCommandHandler handler = new GuiTransferCommandHandler();
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
                    case "settraincartsticket" -> {
                        TicketSetTraincartsTicket handler = new TicketSetTraincartsTicket();
                        handler.handle(sender, args);
                    }
                    default -> returnError(sender, "Unrecognised sub-command \"" + args[1] + "\"");
                }
            }
            case "shop" -> {
                switch (args[1].toLowerCase()) {
                    case "open" -> {
                        ShopOpenCommandHandler handler = new ShopOpenCommandHandler();
                        handler.handle(sender, args);
                    }
                    default -> returnError(sender, "Unrecognised sub-command \"" + args[1] + "\"");
                }
            }
            case "linker" -> {
                switch (args[1].toLowerCase()) {
                    case "create" -> {
                        LinkerCreateCommandHandler handler = new LinkerCreateCommandHandler();
                        handler.handle(sender, args);
                    }
                    case "setdisplayname" -> {
                        LinkerSetDisplayNameCommandHandler handler = new LinkerSetDisplayNameCommandHandler();
                        handler.handle(sender, args);
                    }
                    case "setdestinationpage" -> {
                        LinkerSetDestinationPageCommandHandler handler = new LinkerSetDestinationPageCommandHandler();
                        handler.handle(sender, args);
                    }
                    default -> returnError(sender, "Unrecognised sub-command \"" + args[1] + "\"");
                }
            }
            case "editor" -> {
                switch (args[1].toLowerCase()) {
                    case "add" -> {
                        EditorAddCommandHandler handler = new EditorAddCommandHandler();
                        handler.handle(sender, args);
                    }
                    case "list" -> {
                        EditorListCommandHandler handler = new EditorListCommandHandler();
                        handler.handle(sender, args);
                    }
                    case "remove" -> {
                        EditorRemoveCommandHandler handler = new EditorRemoveCommandHandler();
                        handler.handle(sender, args);
                    }
                    case "removeall" -> {
                        EditorRemoveAllCommandHandler handler = new EditorRemoveAllCommandHandler();
                        handler.handle(sender, args);
                    }
                }
            }
            default -> returnError(sender, "Unrecognised command \"" + args[0] + "\"");
        }
        return true;
    }
}
