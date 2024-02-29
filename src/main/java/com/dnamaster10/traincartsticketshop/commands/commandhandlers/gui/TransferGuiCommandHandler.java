package com.dnamaster10.traincartsticketshop.commands.commandhandlers.gui;

import com.dnamaster10.traincartsticketshop.commands.commandhandlers.AsyncCommandHandler;
import com.dnamaster10.traincartsticketshop.util.Players;
import com.dnamaster10.traincartsticketshop.util.database.AccessorFactory;
import com.dnamaster10.traincartsticketshop.util.database.accessorinterfaces.GuiAccessor;
import com.dnamaster10.traincartsticketshop.util.database.mariadb.MariaDBGuiAccessor;
import com.dnamaster10.traincartsticketshop.util.database.databaseobjects.PlayerDatabaseObject;
import com.dnamaster10.traincartsticketshop.util.exceptions.ModificationException;
import com.dnamaster10.traincartsticketshop.util.exceptions.QueryException;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TransferGuiCommandHandler extends AsyncCommandHandler {
    //Example command: /traincartsticketshop gui transfer <gui name> <player>
    PlayerDatabaseObject otherPlayer;
    private GuiAccessor guiAccessor;
    private Integer guiId;
    @Override
    protected boolean checkSync(CommandSender sender, String[] args) {
        //Check permissions and if player
        if (sender instanceof Player p) {
            if (!p.hasPermission("traincartsticketshop.gui.transfer") && !p.hasPermission("traincartsticketshop.admin.gui.transfer")) {
                returnInsufficientPermissionsError(sender);
                return false;
            }
        }

        //Check syntax
        if (args.length > 4) {
            returnInvalidSubCommandError(sender, args[4]);
            return false;
        }
        if (args.length < 4) {
            returnMissingArgumentsError(sender, "/tshop gui transfer <gui name> <player>");
            return false;
        }
        if (!checkGuiNameSyntax(args[2])) {
            returnGuiNotFoundError(sender, args[2]);
            return false;
        }

        return true;
    }

    @Override
    protected boolean checkAsync(CommandSender sender, String[] args) throws ModificationException, QueryException {
        guiAccessor = AccessorFactory.getGuiAccessor();

        //Get the gui ID and check gui exists
        guiId = guiAccessor.getGuiIdByName(args[2]);
        if (guiId == null) {
            returnGuiNotFoundError(sender, args[2]);
            return false;
        }

        //If sender is player, and they don't have admin transfer rights, check they are owner
        if (sender instanceof Player p) {
            if (!p.hasPermission("traincartsticketshop.admin.gui.transfer")) {
                if (!guiAccessor.checkGuiOwnerByUuid(guiId, p.getUniqueId().toString())) {
                    returnError(sender, "You do not own that gui");
                    return false;
                }
            }
        }

        //Check the other player exists
        otherPlayer = Players.getPlayerByUsername(args[3]);
        if (otherPlayer == null) {
            returnError(sender, "No player with the name \"" + args[3] + "\" could be found");
            return false;
        }
        return true;
    }

    @Override
    protected void execute(CommandSender sender, String[] args) throws ModificationException {
        //Transfer the gui
        guiAccessor.updateGuiOwner(guiId, otherPlayer.uuid());

        //If the new owner is registered as an editor, remove them
        sender.sendMessage(ChatColor.GREEN + "Gui \"" + args[2] + "\" was transferred to " + otherPlayer.username());
    }
}
