package com.dnamaster10.traincartsticketshop.commands.commandhandlers.gui;

import com.dnamaster10.traincartsticketshop.commands.commandhandlers.AsyncCommandHandler;
import com.dnamaster10.traincartsticketshop.commands.commandhandlers.CommandHandler;
import com.dnamaster10.traincartsticketshop.objects.guis.multipageguis.LinkerSearchGui;
import com.dnamaster10.traincartsticketshop.util.Session;
import com.dnamaster10.traincartsticketshop.util.database.GuiAccessor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.StringJoiner;

public class GuiSearchLinkersCommandHandler extends AsyncCommandHandler {
    //Example command: /traincartsticketshop gui searchLinkers <gui name> <search term>
    private String searchTerm;
    private GuiAccessor guiAccessor;
    private Player player;
    @Override
    protected boolean checkSync(CommandSender sender, String[] args) {
        //Check config
        if (!getPlugin().getConfig().getBoolean("AllowGuiSearchLinkers")) {
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
            if (!player.hasPermission("traincartsticketshop.gui.search.searchlinkers")) {
                returnInsufficientPermissionsError(player);
                return false;
            }
        }

        //Check syntax
        if (args.length < 4) {
            returnMissingArgumentsError(player, "/tshop gui searchLinkers <gui name> <search term>");
            return false;
        }
        //Check gui name
        if (!checkGuiNameSyntax(args[2])) {
            returnGuiNotFoundError(player, args[2]);
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
            returnError(player, "Search terms cannot be less than 1 character in length");
            return false;
        }
        return true;
    }

    @Override
    protected boolean checkAsync(CommandSender sender, String[] args) throws SQLException {
        //Check gui exists
        guiAccessor = new GuiAccessor();
        if (!guiAccessor.checkGuiByName(args[2])) {
            returnGuiNotFoundError(player, args[2]);
            return false;
        }
        return true;
    }

    @Override
    protected void execute(CommandSender sender, String[] args) throws SQLException {
        //Get the search gui id
        int searchGuiId = guiAccessor.getGuiIdByName(args[2]);

        //Create the search gui
        LinkerSearchGui gui = new LinkerSearchGui(searchGuiId, searchTerm, player);

        //Open a new session
        Session session = getPlugin().getGuiManager().getNewSession(player);

        //Register the new gui
        session.addGui(gui);

        //Open the new gui
        gui.open();
    }
}
