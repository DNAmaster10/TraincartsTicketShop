package com.dnamaster10.traincartsticketshop.commands.commandhandlers.gui;

import com.dnamaster10.traincartsticketshop.commands.commandhandlers.AsyncCommandHandler;
import com.dnamaster10.traincartsticketshop.objects.guis.multipageguis.TicketSearchGui;
import com.dnamaster10.traincartsticketshop.util.Session;
import com.dnamaster10.traincartsticketshop.util.database.GuiAccessor;
import com.dnamaster10.traincartsticketshop.util.exceptions.DMLException;
import com.dnamaster10.traincartsticketshop.util.exceptions.DQLException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.StringJoiner;

public class GuiSearchTicketsCommandHandler extends AsyncCommandHandler {
    //Example command: /traincartsticketshop gui searchTickets <gui name> <search term>
    private String searchTerm;
    private GuiAccessor guiAccessor;
    private Player player;
    @Override
    protected boolean checkSync(CommandSender sender, String[] args) {
        //Check config
        if (!getPlugin().getConfig().getBoolean("AllowGuiSearchTickets")) {
            returnError(sender, "Searching linkers is disabled on this server");
            return false;
        }
        //Check permission and that sender is player
        if (!(sender instanceof Player p)) {
            returnOnlyPlayersExecuteError(sender);
            return false;
        }
        else {
            player = p;
            if (!player.hasPermission("traincartsticketshop.gui.search.searchtickets")) {
                returnInsufficientPermissionsError(player);
                return false;
            }
        }

        //Check syntax
        if (args.length < 4) {
            returnMissingArgumentsError(player, "/tshop gui searchTickets <gui name> <search term>");
            return false;
        }
        if (!checkGuiNameSyntax(args[2])) {
            returnGuiNotFoundError(player, args[2]);
            return false;
        }

        StringJoiner joiner = new StringJoiner(" ");
        for (int i = 3; i < args.length; i++) {
            joiner.add(args[i]);
        }
        searchTerm = joiner.toString();
        if (searchTerm.length() > 25) {
            returnError(player, "Search term cannot be longer than 25 characters in length");
            return false;
        }
        if (searchTerm.isBlank()) {
            returnError(player, "Search term cannot be less than 1 character in length");
            return false;
        }
        return true;
    }

    @Override
    protected boolean checkAsync(CommandSender sender, String[] args) throws DQLException {
        //Check gui exists
        guiAccessor = new GuiAccessor();
        if (!guiAccessor.checkGuiByName(args[2])) {
            returnGuiNotFoundError(player, args[2]);
            return false;
        }
        return true;
    }

    @Override
    protected void execute(CommandSender sender, String[] args) throws DQLException, DMLException {
        //Get the new gui id
        int guiId = guiAccessor.getGuiIdByName(args[2]);

        //Create new gui
        TicketSearchGui gui = new TicketSearchGui(guiId, searchTerm, 0, (Player) sender);

        //Open a new gui session for the player
        Session session = getPlugin().getGuiManager().getNewSession(player);

        //Register the new gui
        session.addGui(gui);

        //Open the gui to the player
        gui.open();
    }
}
