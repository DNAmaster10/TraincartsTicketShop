package com.dnamaster10.traincartsticketshop.commands.commandhandlers.gui;

import com.dnamaster10.traincartsticketshop.commands.commandhandlers.AsyncCommandHandler;
import com.dnamaster10.traincartsticketshop.util.Players;
import com.dnamaster10.traincartsticketshop.util.database.AccessorFactory;
import com.dnamaster10.traincartsticketshop.util.database.accessorinterfaces.GuiAccessor;
import com.dnamaster10.traincartsticketshop.util.database.accessorinterfaces.GuiEditorsAccessor;
import com.dnamaster10.traincartsticketshop.util.database.databaseobjects.PlayerDatabaseObject;
import com.dnamaster10.traincartsticketshop.util.exceptions.ModificationException;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AddEditorCommandHandler extends AsyncCommandHandler {
    //Command example: /tshop gui addEditor <gui name> <player>
    //This is computed during the async check, so is stored here to be used later in the execute method.
    private PlayerDatabaseObject playerDatabaseObject;
    private GuiEditorsAccessor editorsAccessor;
    private int guiId;
    @Override
    protected boolean checkSync(CommandSender sender, String[] args) {
        //If player check perms
        if (sender instanceof Player p) {
            if (!p.hasPermission("traincartsticketshop.gui.addeditor") && !p.hasPermission("traincartsticketshop.admin.gui.addeditor")) {
                returnInsufficientPermissionsError(sender);
                return false;
            }
        }

        //Check syntax
        if (args.length < 4) {
            returnMissingArgumentsError(sender, "/tshop gui addEditor <gui name> <player>");
            return false;
        }
        if (args.length > 4) {
            returnInvalidSubCommandError(sender, args[4]);
            return false;
        }
        if (!checkGuiNameSyntax(args[2])) {
            returnGuiNotFoundError(sender, args[2]);
            return false;
        }

        return true;
    }

    @Override
    protected boolean checkAsync(CommandSender sender, String[] args) throws ModificationException {
        GuiAccessor guiAccessor = AccessorFactory.getGuiAccessor();

        //Get the gui ID and check that the gui exists
        if (!guiAccessor.checkGuiByName(args[2])) {
            returnGuiNotFoundError(sender, args[2]);
            return false;
        }
        guiId = guiAccessor.getGuiIdByName(args[2]);

        //Check player is owner
        if (sender instanceof Player p && !p.hasPermission("traincartsticketshop.admin.gui.addeditor")) {
            if (!guiAccessor.checkGuiOwnerByUuid(guiId, p.getUniqueId().toString())) {
                returnError(sender, "You do not own that gui");
                return false;
            }
        }

        //Check the editor username is a valid username
        playerDatabaseObject = Players.getPlayerByUsername(args[3]);
        if (playerDatabaseObject == null) {
            returnError(sender, "No player with the username \"" + args[3] + "\" could be found");
            return false;
        }

        //Check that the new player isn't the same player as the owner
        if (guiAccessor.checkGuiOwnerByUuid(guiId, playerDatabaseObject.uuid())) {
            returnError(sender, "Player \"" + playerDatabaseObject.username() + "\" already owns that gui");
            return false;
        }

        editorsAccessor = AccessorFactory.getGuiEditorsAccessor();
        //Check that the player isn't already an editor
        if (editorsAccessor.checkGuiEditorByUuid(guiId, playerDatabaseObject.uuid())) {
            returnError(sender, "Player \"" + playerDatabaseObject.username() + "\" is already an editor of that gui");
            return false;
        }
        return true;
    }

    @Override
    protected void execute(CommandSender sender, String[] args) throws ModificationException {
        editorsAccessor.addGuiEditor(guiId, playerDatabaseObject.uuid());
        sender.sendMessage(ChatColor.GREEN + "Player \"" + playerDatabaseObject.username() + "\" was registered as an editor for gui \"" + args[2] + "\"");
    }
}