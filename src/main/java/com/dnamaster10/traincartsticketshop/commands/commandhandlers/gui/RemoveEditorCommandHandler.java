package com.dnamaster10.traincartsticketshop.commands.commandhandlers.gui;

import com.dnamaster10.traincartsticketshop.commands.commandhandlers.AsyncCommandHandler;
import com.dnamaster10.traincartsticketshop.util.Players;
import com.dnamaster10.traincartsticketshop.util.database.GuiAccessor;
import com.dnamaster10.traincartsticketshop.util.database.GuiEditorsAccessor;
import com.dnamaster10.traincartsticketshop.util.database.databaseobjects.PlayerDatabaseObject;
import com.dnamaster10.traincartsticketshop.util.exceptions.DMLException;
import com.dnamaster10.traincartsticketshop.util.exceptions.DQLException;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static com.dnamaster10.traincartsticketshop.TraincartsTicketShop.getPlugin;

public class RemoveEditorCommandHandler extends AsyncCommandHandler {
    //Example command: /tshop editor remove <player_name> <gui_name>
    private PlayerDatabaseObject editorDatabaseObject;
    private GuiEditorsAccessor editorsAccessor;
    private Integer guiId;
    @Override
    protected boolean checkSync(CommandSender sender, String[] args) {
        //Check sender perms
        if (sender instanceof Player p) {
            if (!p.hasPermission("traincartsticketshop.gui.removeEditor") && !p.hasPermission("traincartsticketshop.admin.gui.removeEditor")) {
                returnInsufficientPermissionsError(sender);
                return false;
            }
        }

        //Check syntax
        if (args.length < 4) {
            returnMissingArgumentsError(sender, "/tshop gui removeEditor <player username> <gui name>");
            return false;
        }
        if (args.length > 4) {
            returnInvalidSubCommandError(sender, args[4]);
            return false;
        }
        if (!checkGuiNameSyntax(args[3])) {
            returnGuiNotFoundError(sender, args[3]);
            return false;
        }
        return true;
    }

    @Override
    protected boolean checkAsync(CommandSender sender, String[] args) throws DQLException, DMLException {
        GuiAccessor guiAccessor = new GuiAccessor();

        //Get the guiID and check that the gui exists
        guiId = guiAccessor.getGuiIdByName(args[3]);
        if (guiId == null) {
            returnGuiNotFoundError(sender, args[3]);
            return false;
        }

        //If player, check that they own the gui
        if (sender instanceof Player p) {
            if (!p.hasPermission("traincartsticketshop.admin.gui.removeEditor")) {
                if (!guiAccessor.checkGuiOwnerByUuid(guiId, p.getUniqueId().toString())) {
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

        editorsAccessor = new GuiEditorsAccessor();
        //Check that the editor exists in the editors table
        if (!editorsAccessor.checkGuiEditorByUuid(guiId, editorDatabaseObject.uuid())) {
            returnError(sender, "Player \"" + editorDatabaseObject.username() + "\" is not a registered editor for gui \"" + args[3] + "\"");
            return false;
        }
        return true;
    }

    @Override
    protected void execute(CommandSender sender, String[] args) throws DMLException {
        //Remove the editor
        editorsAccessor.removeGuiEditor(guiId, editorDatabaseObject.uuid());
        sender.sendMessage(ChatColor.GREEN + "Player \"" + editorDatabaseObject.username() + "\" is no longer an editor of gui \"" + args[3] + "\"");
    }
}