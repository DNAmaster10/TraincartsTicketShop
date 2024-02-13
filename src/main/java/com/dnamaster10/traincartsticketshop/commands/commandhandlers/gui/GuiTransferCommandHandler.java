package com.dnamaster10.traincartsticketshop.commands.commandhandlers.gui;

import com.dnamaster10.traincartsticketshop.commands.commandhandlers.CommandHandler;
import com.dnamaster10.traincartsticketshop.util.Players;
import com.dnamaster10.traincartsticketshop.util.database.GuiAccessor;
import com.dnamaster10.traincartsticketshop.util.database.databaseobjects.PlayerDatabaseObject;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class GuiTransferCommandHandler extends CommandHandler {
    //Example command: /traincartsticketshop gui transfer <gui name> <player>
    PlayerDatabaseObject otherPlayer;
    private GuiAccessor guiAccessor;
    @Override
    protected boolean checkSync(CommandSender sender, String[] args) {
        if (!getPlugin().getConfig().getBoolean("AllowGuiTransfer")) {
            returnError(sender, "Gui transfer is disabled on this server");
            return false;
        }

        //Check permissions and if player
        if (sender instanceof Player p) {
            if (!p.hasPermission("traincartsticketshop.gui.transfer") && !p.hasPermission("traincartsticketshop.admin.gui.transfer")) {
                returnError(sender, "You do not have permission to perform that action");
                return false;
            }
        }

        //Check syntax
        if (args.length > 4) {
            returnError(sender, "Invalid sub-command \"" + args[4] + "\"");
            return false;
        }
        if (args.length < 4) {
            returnError(sender, "Missing argument(s): /traincartsticketshop gui transfer <gui name> <player>");
            return false;
        }
        if (!checkGuiNameSyntax(args[2])) {
            returnGuiNotFoundError(sender, args[2]);
            return false;
        }

        return true;
    }

    @Override
    protected boolean checkAsync(CommandSender sender, String[] args) throws SQLException {
        //Check that the gui exists
        guiAccessor = new GuiAccessor();
        if (!guiAccessor.checkGuiByName(args[2])) {
            returnGuiNotFoundError(sender, args[2]);
            return false;
        }

        //If sender is player, and they don't have admin transfer rights, check they are owner
        if (sender instanceof Player p) {
            if (!p.hasPermission("traincartsticketshop.admin.gui.transfer")) {
                if (!guiAccessor.checkGuiOwnershipByUuid(args[2], p.getUniqueId().toString())) {
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
    protected void execute(CommandSender sender, String[] args) throws SQLException {
        //Transfer the gui
        guiAccessor.updateGuiOwner(args[2], otherPlayer.getUuid());

        //If the new owner is registered as an editor, remove them
        int guiId = guiAccessor.getGuiIdByName(args[2]);
        guiAccessor.removeGuiEditorByUuid(guiId, otherPlayer.getUuid());

        sender.sendMessage(ChatColor.GREEN + "Gui \"" + args[2] + "\" was transferred to " + otherPlayer.getUsername());
    }
}
