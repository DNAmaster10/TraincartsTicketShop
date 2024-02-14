package com.dnamaster10.traincartsticketshop.commands.commandhandlers.editor;

import com.dnamaster10.traincartsticketshop.commands.commandhandlers.AsyncCommandHandler;
import com.dnamaster10.traincartsticketshop.commands.commandhandlers.CommandHandler;
import com.dnamaster10.traincartsticketshop.util.Players;
import com.dnamaster10.traincartsticketshop.util.database.GuiAccessor;
import com.dnamaster10.traincartsticketshop.util.database.databaseobjects.PlayerDatabaseObject;
import com.dnamaster10.traincartsticketshop.util.exceptions.DMLException;
import com.dnamaster10.traincartsticketshop.util.exceptions.DQLException;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class EditorRemoveAllCommandHandler extends AsyncCommandHandler {
    //TODO command needs finishing
    //Example command: /traincartsticketshop editor removeAll <gui name>
    private GuiAccessor guiAccessor;
    @Override
    protected boolean checkSync(CommandSender sender, String[] args) {
        //Check config
        if (!getPlugin().getConfig().getBoolean("AllowEditorRemoveAll")) {
            returnError(sender, "Removing all editors is disabled on this server");
            return false;
        }

        //If player check perms
        if (sender instanceof Player p) {
            if (!p.hasPermission("traincartsticketshop.editor.removeall") && !p.hasPermission("traincartsticketshop.admin.editor.removeall")) {
                returnError(sender, "You do not have permission to perform that action");
                return false;
            }
        }

        //Check syntax
        if (args.length < 3) {
            returnError(sender, "Missing argument(s): /traincartsticketshop editor removeAll <gui name>");
            return false;
        }
        if (args.length > 3) {
            returnError(sender, "Invalid sub-command \"" + args[3] + "\"");
            return false;
        }
        if (!checkGuiNameSyntax(args[2])) {
            returnGuiNotFoundError(sender, args[2]);
            return false;
        }
        return true;
    }

    @Override
    protected boolean checkAsync(CommandSender sender, String[] args) throws DQLException {
        //Check gui exists
        guiAccessor = new GuiAccessor();
        if (!guiAccessor.checkGuiByName(args[2])) {
            returnGuiNotFoundError(sender, args[2]);
            return false;
        }

        //Check player is owner or isn't admin
        if (sender instanceof Player p) {
            if (!p.hasPermission("traincartsticketshop.admin.editor.removeall")) {
                if (!guiAccessor.checkGuiOwnershipByUuid(args[2], p.getUniqueId().toString())) {
                    returnError(sender, "You do not own that gui");
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    protected void execute(CommandSender sender, String[] args) throws DQLException, DMLException {
        int guiId = guiAccessor.getGuiIdByName(args[2]);
        guiAccessor.removeAllGuiEditors(guiId);
    }
}
