package com.dnamaster10.traincartsticketshop.commands.commandhandlers.gui;

import com.dnamaster10.traincartsticketshop.commands.commandhandlers.AsyncCommandHandler;
import com.dnamaster10.traincartsticketshop.util.database.AccessorFactory;
import com.dnamaster10.traincartsticketshop.util.database.accessorinterfaces.GuiAccessor;
import com.dnamaster10.traincartsticketshop.util.database.accessorinterfaces.LinkerAccessor;
import com.dnamaster10.traincartsticketshop.util.database.accessorinterfaces.TicketAccessor;
import com.dnamaster10.traincartsticketshop.util.exceptions.QueryException;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.md_5.bungee.api.chat.*;

public class GuiInfoCommandHandler extends AsyncCommandHandler {
    //Example command: /tshop gui checkInfo <gui name>

    GuiAccessor guiAccessor;
    Integer guiId;
    @Override
    protected boolean checkSync(CommandSender sender, String[] args) {
        //Check permissions
        if (sender instanceof Player p) {
            if (!p.hasPermission("traincartsticketshop.gui.info")) {
                returnInsufficientPermissionsError(p);
                return false;
            }
        }

        //Check syntax
        if (args.length < 3) {
            returnMissingArgumentsError(sender, "/tshop gui info <gui name>");
            return false;
        }
        if (args.length > 3) {
            returnInvalidSubCommandError(sender, args[3]);
            return false;
        }
        if (!checkGuiNameSyntax(args[2])) {
            returnGuiNotFoundError(sender, args[2]);
            return false;
        }

        return true;
    }

    @Override
    protected boolean checkAsync(CommandSender sender, String[] args) throws QueryException {
        guiAccessor = AccessorFactory.getGuiAccessor();

        guiId = guiAccessor.getGuiIdByName(args[2]);
        if (guiId == null) {
            returnGuiNotFoundError(sender, args[2]);
            return false;
        }

        return true;
    }

    @Override
    protected void execute(CommandSender sender, String[] args) throws QueryException {
        //Fetch info
        String guiName = guiAccessor.getGuiNameById(guiId);
        String owner = guiAccessor.getOwnerUsername(guiId);
        int totalPages = guiAccessor.getHighestPageNumber(guiId) + 1;

        TicketAccessor ticketAccessor = AccessorFactory.getTicketAccessor();
        LinkerAccessor linkerAccessor = AccessorFactory.getLinkerAccessor();

        int totalTickets = ticketAccessor.getTotalTickets(guiId);
        int totalLinkers = linkerAccessor.getTotalLinkers(guiId);

        //Send info to player
        TextComponent line;
        line = new TextComponent(ChatColor.AQUA + "Info for gui \"" + guiName + "\":");
        sender.spigot().sendMessage(line);

        line = new TextComponent(ChatColor.WHITE + "| Owner: " + owner);
        sender.spigot().sendMessage(line);
        line = new TextComponent(ChatColor.WHITE + "| Pages: " + totalPages);
        sender.spigot().sendMessage(line);
        line = new TextComponent(ChatColor.WHITE + "| Tickets: " + totalTickets);
        sender.spigot().sendMessage(line);
        line = new TextComponent(ChatColor.WHITE + "| Linkers: " + totalLinkers);
        sender.spigot().sendMessage(line);
    }
}
