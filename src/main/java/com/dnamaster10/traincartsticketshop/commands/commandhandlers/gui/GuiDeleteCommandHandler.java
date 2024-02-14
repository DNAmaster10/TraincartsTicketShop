package com.dnamaster10.traincartsticketshop.commands.commandhandlers.gui;

import com.dnamaster10.traincartsticketshop.commands.commandhandlers.AsyncCommandHandler;
import com.dnamaster10.traincartsticketshop.commands.commandhandlers.CommandHandler;
import com.dnamaster10.traincartsticketshop.util.database.GuiAccessor;
import com.dnamaster10.traincartsticketshop.util.database.LinkerAccessor;
import com.dnamaster10.traincartsticketshop.util.database.TicketAccessor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class GuiDeleteCommandHandler extends AsyncCommandHandler {
    //Command example: /traincartsticketshop gui delete <gui_name>
    private GuiAccessor guiAccessor;
    @Override
    protected boolean checkSync(CommandSender sender, String[] args) {
        //Synchronous checks
        //Check config
        if (!getPlugin().getConfig().getBoolean("AllowGuiDelete")) {
            returnError(sender, "Gui deletion is disabled on this server");
            return false;
        }

        //Check permissions if player
        if (sender instanceof Player p) {
            if (!p.hasPermission("traincartsticketshop.gui.delete")) {
                returnInsufficientPermissionsError(sender);
                return false;
            }
        }

        //Check syntax
        if (args.length > 3) {
            returnInvalidSubCommandError(sender, args[3]);
            return false;
        }
        if (args.length < 3) {
            returnMissingArgumentsError(sender, "/tshop gui delete <gui name>");
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
        //Check that GUI exists
        guiAccessor = new GuiAccessor();
        if (!guiAccessor.checkGuiByName(args[2])) {
            returnGuiNotFoundError(sender, args[2]);
            return false;
        }

        //If sender is player, check they are owner
        if (sender instanceof Player p) {
            if (!guiAccessor.checkGuiOwnershipByUuid(args[2], p.getUniqueId().toString())) {
                returnError(sender, "You must be the owner of the gui in order to delete it");
                return false;
            }
        }
        return false;
    }

    @Override
    protected void execute(CommandSender sender, String[] args) throws SQLException {
        //Create and open a delete confirm gui

    }
}
