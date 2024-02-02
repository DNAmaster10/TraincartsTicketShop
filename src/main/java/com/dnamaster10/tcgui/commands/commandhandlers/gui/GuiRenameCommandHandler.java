package com.dnamaster10.tcgui.commands.commandhandlers.gui;

import com.dnamaster10.tcgui.commands.commandhandlers.CommandHandler;
import com.dnamaster10.tcgui.util.database.GuiAccessor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class GuiRenameCommandHandler extends CommandHandler<Exception> {
    //Example command: /tcgui gui rename old_name new_name
    private GuiAccessor guiAccessor;

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
            returnError(sender, "Missing argument(s): /tcgui gui rename <old_name> <new_name>");
            return false;
        }
        if (args.length > 4) {
            returnError(sender, "Unrecognised sub-command \"" + args[4] + "\"");
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
            if (!p.hasPermission("tcgui.gui.rename") && !p.hasPermission("tcgui.admin.gui.rename")) {
                returnError(sender, "You do not have permission to perform that action");
                return false;
            }
        }

        //If all checks have passed, return true
        return true;
    }

    @Override
    protected boolean checkAsync(CommandSender sender, String[] args) throws SQLException {
        guiAccessor = new GuiAccessor();

        //First check that the gui exists
        if (!guiAccessor.checkGuiByName(args[2])) {
            returnGuiNotFoundError(sender, args[2]);
            return false;
        }

        //If sender is player, check that player is an editor of that gui if they don't have admin perms
        if (sender instanceof Player p) {
            if (!p.hasPermission("tcgui.admin.gui.rename")) {
                if (!guiAccessor.playerCanEdit(args[2], p.getUniqueId().toString())) {
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
    protected void execute(CommandSender sender, String[] args) throws SQLException {
        guiAccessor.updateGuiName(args[2], args[3]);
        sender.sendMessage(ChatColor.GREEN + "Gui \"" + args[2] + "\" was successfully renamed to \"" + args[3] + "\"");
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        if (!checkSync(sender, args)) {
            return;
        }
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            try {
                if (!checkAsync(sender, args)) {
                    return;
                }
                execute(sender, args);
            } catch (SQLException e) {
                getPlugin().reportSqlError(sender, e);
            }
        });
    }
}
