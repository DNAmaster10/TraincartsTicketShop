package com.dnamaster10.traincartsticketshop.commands.commandhandlers.editor;

import com.dnamaster10.traincartsticketshop.commands.commandhandlers.CommandHandler;
import com.dnamaster10.traincartsticketshop.util.Players;
import com.dnamaster10.traincartsticketshop.util.database.GuiAccessor;
import com.dnamaster10.traincartsticketshop.util.database.databaseobjects.PlayerDatabaseObject;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class EditorRemoveCommandHandler extends CommandHandler {
    //Example command: /traincartsticketshop editor remove <player_name> <gui_name>
    PlayerDatabaseObject editorDatabaseObject;
    GuiAccessor guiAccessor;
    @Override
    protected boolean checkSync(CommandSender sender, String[] args) {
        //Check config
        if (!getPlugin().getConfig().getBoolean("AllowEditorRemove")) {
            returnError(sender, "Removing editor is disabled on this server");
            return false;
        }

        //If player check perms
        if (sender instanceof Player p) {
            if (!p.hasPermission("traincartsticketshop.editor.remove") && !p.hasPermission("traincartsticketshop.admin.editor.remove")) {
                returnError(sender, "You do not have permission to perform that action");
                return false;
            }
        }

        //Check syntax
        if (args.length < 4) {
            returnError(sender, "Missing argument(s): /traincartsticketshop editor add <gui_name> <username");
            return false;
        }
        if (args.length > 4) {
            returnError(sender, "Invalid sub-command \"" + args[4] + "\"");
            return false;
        }
        if (!checkGuiNameSyntax(args[3])) {
            returnGuiNotFoundError(sender, args[3]);
            return false;
        }
        return true;
    }

    @Override
    protected boolean checkAsync(CommandSender sender, String[] args) throws SQLException {
        //Check that gui exists
        guiAccessor = new GuiAccessor();
        if (!guiAccessor.checkGuiByName(args[3])) {
            returnGuiNotFoundError(sender, args[3]);
            return false;
        }

        //If player, check that they own the gui
        if (sender instanceof Player p) {
            if (!p.hasPermission("traincartsticketshop.admin.editor.remove")) {
                if (!guiAccessor.checkGuiOwnershipByUuid(args[3], p.getUniqueId().toString())) {
                    returnError(sender, "You do not own that gui");
                    return false;
                }
            }
        }

       //Check that editor is a valid username and that they are a registered editor of the gui
        editorDatabaseObject = Players.getPlayerByUsername(args[2]);
        if (editorDatabaseObject == null) {
            returnError(sender, "No player with the username \"" + args[2] + "\" could be found");
            return false;
        }
        //Check that the editor exists in the editors table
        if (!guiAccessor.checkGuiEditorByUuid(args[3], editorDatabaseObject.getUuid())) {
            returnError(sender, "Player \"" + args[2] + "\" is not a registered editor for gui \"" + args[3] + "\"");
            return false;
        }
        return true;
    }

    @Override
    protected void execute(CommandSender sender, String[] args) throws SQLException {
        //Remove the editor
        int guiId = guiAccessor.getGuiIdByName(args[3]);
        guiAccessor.removeGuiEditorByUuid(guiId, editorDatabaseObject.getUuid());
    }
}
