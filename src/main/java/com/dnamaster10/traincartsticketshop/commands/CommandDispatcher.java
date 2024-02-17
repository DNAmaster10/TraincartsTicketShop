package com.dnamaster10.traincartsticketshop.commands;

import com.dnamaster10.traincartsticketshop.commands.commandhandlers.gui.RemoveEditorCommandHandler;
import com.dnamaster10.traincartsticketshop.commands.commandhandlers.gui.*;
import com.dnamaster10.traincartsticketshop.commands.commandhandlers.gui.AddEditorCommandHandler;
import com.dnamaster10.traincartsticketshop.commands.commandhandlers.gui.ListEditorsCommandHandler;
import com.dnamaster10.traincartsticketshop.commands.commandhandlers.linker.LinkerCreateCommandHandler;
import com.dnamaster10.traincartsticketshop.commands.commandhandlers.linker.LinkerSetDisplayNameCommandHandler;
import com.dnamaster10.traincartsticketshop.commands.commandhandlers.linker.LinkerSetDestinationPageCommandHandler;
import com.dnamaster10.traincartsticketshop.commands.commandhandlers.gui.OpenGuiCommandHandler;
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
                    case "addeditor" -> new AddEditorCommandHandler().handle(sender, args);
                    case "checkguiinfo" -> new CheckGuiInfoCommandhandler().handle(sender, args);
                    case "create" -> new CreateGuiCommandHandler().handle(sender, args);
                    case "delete" -> new DeleteGuiCommandHandler().handle(sender, args);
                    case "edit" -> new EditGuiCommandHandler().handle(sender, args);
                    case "listeditors" -> new ListEditorsCommandHandler().handle(sender, args);
                    case "open" -> new OpenGuiCommandHandler().handle(sender, args);
                    case "removeeditor" -> new RemoveEditorCommandHandler().handle(sender, args);
                    case "rename" -> new RenameGuiCommandHandler().handle(sender, args);
                    case "setdisplayname" -> new SetGuiDisplayNameCommandHandler().handle(sender, args);
                    case "searchlinkers" -> new SearchLinkersCommandHandler().handle(sender, args);
                    case "searchtickets" -> new SearchTicketsCommandHandler().handle(sender, args);
                    case "transfer" -> new TransferGuiCommandHandler().handle(sender, args);

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
            default -> returnInvalidSubCommandError(sender, args[1]);
        }
        return true;
    }
}
