package com.dnamaster10.traincartsticketshop.commands.commandhandlers.gui;

import com.dnamaster10.traincartsticketshop.commands.commandhandlers.AsyncCommandHandler;
import com.dnamaster10.traincartsticketshop.objects.guis.multipageguis.TicketSearchGui;
import com.dnamaster10.traincartsticketshop.util.Session;
import com.dnamaster10.traincartsticketshop.util.database.mariadb.MariaDBGuiAccessor;
import com.dnamaster10.traincartsticketshop.util.exceptions.QueryException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.StringJoiner;

import static com.dnamaster10.traincartsticketshop.TraincartsTicketShop.getPlugin;

public class SearchTicketsCommandHandler extends AsyncCommandHandler {
    //Example command: /traincartsticketshop gui searchTickets <gui name> <search term>
    private String searchTerm;
    private MariaDBGuiAccessor guiAccessor;
    private Player player;
    @Override
    protected boolean checkSync(CommandSender sender, String[] args) {
        //Check permission and that sender is player
        if (!(sender instanceof Player)) {
            returnOnlyPlayersExecuteError(sender);
            return false;
        }
        player = (Player) sender;

        if (!player.hasPermission("traincartsticketshop.gui.search.tickets")) {
            returnInsufficientPermissionsError(player);
            return false;
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
    protected boolean checkAsync(CommandSender sender, String[] args) throws QueryException {
        //Check gui exists
        guiAccessor = new MariaDBGuiAccessor();
        if (!guiAccessor.checkGuiByName(args[2])) {
            returnGuiNotFoundError(player, args[2]);
            return false;
        }
        return true;
    }

    @Override
    protected void execute(CommandSender sender, String[] args) throws QueryException {
        //Get the new gui id
        int guiId = guiAccessor.getGuiIdByName(args[2]);

        //Create new gui
        TicketSearchGui gui = new TicketSearchGui(guiId, searchTerm, 0, player);

        //Open a new gui session for the player
        Session session = getPlugin().getGuiManager().getNewSession(player);

        //Register the new gui
        session.addGui(gui);

        //Open the gui to the player
        gui.open();
    }
}
