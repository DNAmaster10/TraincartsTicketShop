package com.dnamaster10.traincartsticketshop.commands.commandhandlers.gui;

import com.dnamaster10.traincartsticketshop.commands.commandhandlers.AsyncCommandHandler;
import com.dnamaster10.traincartsticketshop.util.database.AccessorFactory;
import com.dnamaster10.traincartsticketshop.util.database.accessorinterfaces.GuiAccessor;
import com.dnamaster10.traincartsticketshop.util.exceptions.ModificationException;
import com.dnamaster10.traincartsticketshop.util.exceptions.QueryException;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RenameGuiCommandHandler extends AsyncCommandHandler {
    //Example command: /traincartsticketshop gui rename old_name new_name
    //TODO should probably only be renameable by the owner
    private GuiAccessor guiAccessor;
    private int guiId;

    @Override
    protected boolean checkSync(CommandSender sender, String[] args) {
        //Check syntax
        if (args.length < 4) {
            returnMissingArgumentsError(sender, "/tshop gui rename <old name> <new name>");
            return false;
        }
        if (args.length > 4) {
            returnInvalidSubCommandError(sender, args[4]);
            return false;
        }
        if (args[3].length() > 20) {
            returnError(sender, "Gui names cannot be more than 20 characters in length");
            return false;
        }
        if (args[3].length() < 3) {
            returnError(sender, "Gui names cannot be less than 3 characters in length");
            return false;
        }
        //Check syntax of old gui name to save on database calls
        if (!checkGuiNameSyntax(args[2])) {
            returnGuiNotFoundError(sender, args[2]);
            return false;
        }
        if (!checkStringFormat(args[3])) {
            returnError(sender, "Gui names can only contain letters Aa - Zz, numbers, underscores and dashes");
            return false;
        }

        //Check permissions
        if (sender instanceof Player p) {
            if (!p.hasPermission("traincartsticketshop.gui.rename") && !p.hasPermission("traincartsticketshop.admin.gui.rename")) {
                returnInsufficientPermissionsError(sender);
                return false;
            }
        }

        //If all checks have passed, return true
        return true;
    }

    @Override
    protected boolean checkAsync(CommandSender sender, String[] args) throws QueryException {
        guiAccessor = AccessorFactory.getGuiAccessor();

        //Get the gui ID and check that it exists
        if (!guiAccessor.checkGuiByName(args[2])) {
            returnGuiNotFoundError(sender, args[2]);
            return false;
        }
        guiId = guiAccessor.getGuiIdByName(args[2]);

        //If sender is player, check that player is an editor of that gui if they don't have admin perms
        if (sender instanceof Player p) {
            if (!p.hasPermission("traincartsticketshop.admin.gui.rename")) {
                if (!guiAccessor.playerCanEdit(guiId, p.getUniqueId().toString())) {
                    returnError(sender, "You do not have permission to edit that gui. Request that the owner adds you as an editor before making any changes");
                    return false;
                }
            }
        }

        //Check that the new gui name doesn't already exist
        if (guiAccessor.checkGuiByName(args[3])) {
            returnError(sender, "A gui with name \"" + args[3] + "\" already exists");
            return false;
        }
        return true;
    }

    @Override
    protected void execute(CommandSender sender, String[] args) throws ModificationException {
        guiAccessor.updateGuiName(guiId, args[3]);
        sender.sendMessage(ChatColor.GREEN + "Gui \"" + args[2] + "\" was successfully renamed to \"" + args[3] + "\"");
    }
}
