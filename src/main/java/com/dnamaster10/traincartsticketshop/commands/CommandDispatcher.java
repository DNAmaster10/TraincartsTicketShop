package com.dnamaster10.traincartsticketshop.commands;

import com.dnamaster10.traincartsticketshop.commands.commandhandlers.editor.EditorRemoveCommandHandler;
import com.dnamaster10.traincartsticketshop.commands.commandhandlers.gui.*;
import com.dnamaster10.traincartsticketshop.commands.commandhandlers.editor.EditorAddCommandHandler;
import com.dnamaster10.traincartsticketshop.commands.commandhandlers.editor.EditorListCommandHandler;
import com.dnamaster10.traincartsticketshop.commands.commandhandlers.linker.LinkerCreateCommandHandler;
import com.dnamaster10.traincartsticketshop.commands.commandhandlers.linker.LinkerSetDisplayNameCommandHandler;
import com.dnamaster10.traincartsticketshop.commands.commandhandlers.linker.LinkerSetDestinationPageCommandHandler;
import com.dnamaster10.traincartsticketshop.commands.commandhandlers.gui.GuiOpenCommandHandler;
import com.dnamaster10.traincartsticketshop.commands.commandhandlers.ticket.TicketCreateCommandHandler;
import com.dnamaster10.traincartsticketshop.commands.commandhandlers.ticket.TicketSetDisplayNameCommandHandler;
import com.dnamaster10.traincartsticketshop.commands.commandhandlers.ticket.TicketSetTraincartsTicket;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class CommandDispatcher implements CommandExecutor {
    private void returnError(CommandSender sender, String error) {
        sender.sendMessage(ChatColor.RED + error);
    }
    private void returnInvalidSubCommandError(CommandSender sender, String argument) {
        returnError(sender, "Invalid sub-command \"" + argument + "\"");
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        //Check that a sub-command was provided
        if (args.length < 2) {
            returnError(sender, "Please enter a valid sub-command");
            return true;
        }

        //Decide how to handle command
        switch (args[0].toLowerCase()) {
            case "gui" -> {
                switch (args[1].toLowerCase()) {
                    case "create" -> new GuiCreateCommandHandler().handle(sender, args);
                    case "delete" -> new GuiDeleteCommandHandler().handle(sender, args);
                    case "open" -> new GuiOpenCommandHandler().handle(sender, args);
                    case "rename" -> new GuiRenameCommandHandler().handle(sender, args);
                    case "edit" -> new GuiEditCommandHandler().handle(sender, args);
                    case "setdisplayname" -> new GuiSetDisplayNameCommandHandler().handle(sender, args);
                    case "searchlinkers" -> new GuiSearchLinkersCommandHandler().handle(sender, args);
                    case "searchtickets" -> new GuiSearchTicketsCommandHandler().handle(sender, args);
                    case "transfer" -> new GuiTransferCommandHandler().handle(sender, args);

                    default -> returnInvalidSubCommandError(sender, args[1]);
                }
            }
            case "ticket" -> {
                switch (args[1].toLowerCase()) {
                    case "create" -> new TicketCreateCommandHandler().handle(sender, args);
                    case "setdisplayname" -> new TicketSetDisplayNameCommandHandler().handle(sender, args);
                    case "settraincartsticket" -> new TicketSetTraincartsTicket().handle(sender, args);

                    default -> returnInvalidSubCommandError(sender, args[1]);
                }
            }
            case "linker" -> {
                switch (args[1].toLowerCase()) {
                    case "create" -> new LinkerCreateCommandHandler().handle(sender, args);
                    case "setdisplayname" -> new LinkerSetDisplayNameCommandHandler().handle(sender, args);
                    case "setdestinationpage" -> new LinkerSetDestinationPageCommandHandler().handle(sender, args);

                    default -> returnInvalidSubCommandError(sender, args[1]);
                }
            }
            case "editor" -> {
                switch (args[1].toLowerCase()) {
                    case "add" -> new EditorAddCommandHandler().handle(sender, args);
                    case "list" -> new EditorListCommandHandler().handle(sender, args);
                    case "remove" -> new EditorRemoveCommandHandler().handle(sender, args);

                    default -> returnInvalidSubCommandError(sender, args[1]);
                }
            }

            default -> returnInvalidSubCommandError(sender, args[1]);
        }
        return true;
    }
}
