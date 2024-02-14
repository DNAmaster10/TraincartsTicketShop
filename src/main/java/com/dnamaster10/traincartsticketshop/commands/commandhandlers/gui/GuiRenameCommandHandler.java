package com.dnamaster10.traincartsticketshop.commands.commandhandlers.gui;

import com.dnamaster10.traincartsticketshop.commands.commandhandlers.AsyncCommandHandler;
import com.dnamaster10.traincartsticketshop.util.database.GuiAccessor;
import com.dnamaster10.traincartsticketshop.util.exceptions.DMLException;
import com.dnamaster10.traincartsticketshop.util.exceptions.DQLException;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GuiRenameCommandHandler extends AsyncCommandHandler {
    //Example command: /traincartsticketshop gui rename old_name new_name
    private GuiAccessor guiAccessor;
    private Integer guiId;

    @Override
    protected boolean checkSync(CommandSender sender, String[] args) {
        //This command can be run by the player as well as other interfaces.
        //We first check things which apply to both

        //Check config
        if (!getPlugin().getConfig().getBoolean("AllowGuiRename")) {
            returnError(sender, "Gui renaming is disabled on this server");
            return false;
        }

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
    protected boolean checkAsync(CommandSender sender, String[] args) throws DQLException {
        guiAccessor = new GuiAccessor();

        //Get the gui ID and check that it exists
        guiId = guiAccessor.getGuiIdByName(args[2]);
        if (guiId == null) {
            returnGuiNotFoundError(sender, args[2]);
            return false;
        }

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
    protected void execute(CommandSender sender, String[] args) throws DMLException {
        guiAccessor.updateGuiName(args[2], args[3]);
        sender.sendMessage(ChatColor.GREEN + "Gui \"" + args[2] + "\" was successfully renamed to \"" + args[3] + "\"");
    }
}
