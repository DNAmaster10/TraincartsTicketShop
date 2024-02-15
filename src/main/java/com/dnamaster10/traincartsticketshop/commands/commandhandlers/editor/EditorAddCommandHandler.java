package com.dnamaster10.traincartsticketshop.commands.commandhandlers.editor;

import com.dnamaster10.traincartsticketshop.commands.commandhandlers.AsyncCommandHandler;
import com.dnamaster10.traincartsticketshop.util.Players;
import com.dnamaster10.traincartsticketshop.util.database.GuiAccessor;
import com.dnamaster10.traincartsticketshop.util.database.databaseobjects.PlayerDatabaseObject;
import com.dnamaster10.traincartsticketshop.util.exceptions.DMLException;
import com.dnamaster10.traincartsticketshop.util.exceptions.DQLException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EditorAddCommandHandler extends AsyncCommandHandler {
    //Command example: /traincartsticketshop editor add <player_name> <gui_name>
    //This is computed during the async check, so is stored here to be used later in the execute method.
    private PlayerDatabaseObject playerDatabaseObject;
    private GuiAccessor guiAccessor;
    private Integer guiId;
    @Override
    protected boolean checkSync(CommandSender sender, String[] args) {
        //Check config
        if (!getPlugin().getConfig().getBoolean("AllowEditorAdd")) {
            returnError(sender, "Adding editor is disabled on this server");
            return false;
        }

        //If player check perms
        if (sender instanceof Player p) {
            if (!p.hasPermission("traincartsticketshop.editor.add") && !p.hasPermission("traincartsticketshop.admin.editor.add")) {
                returnInsufficientPermissionsError(sender);
                return false;
            }
        }

        //Check syntax
        if (args.length < 4) {
            returnMissingArgumentsError(sender, "/tshop editor add <gui_name> <username>");
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
        guiAccessor = new GuiAccessor();

        //Get the gui ID and check that the gui exists
        guiId = guiAccessor.getGuiIdByName(args[3]);
        if (guiId == null) {
            returnGuiNotFoundError(sender, args[3]);
            return false;
        }

        //Check player is owner
        if (sender instanceof Player p && !p.hasPermission("traincartsticketshop.admin.editor.add")) {
            if (!guiAccessor.checkGuiOwnerByUuid(guiId, p.getUniqueId().toString())) {
                returnError(sender, "You do not own that gui");
                return false;
            }
        }

        //Check the editor username is a valid username
        playerDatabaseObject = Players.getPlayerByUsername(args[2]);
        if (playerDatabaseObject == null) {
            returnError(sender, "No player with the username \"" + args[2] + "\" could be found");
            return false;
        }

        //Check that the new player isn't the same player as the owner
        if (guiAccessor.checkGuiOwnerByUuid(guiId, playerDatabaseObject.getUuid())) {
            returnError(sender, "Player \"" + playerDatabaseObject.getUsername() + "\" already owns that gui");
            return false;
        }
        return true;
    }

    @Override
    protected void execute(CommandSender sender, String[] args) throws DQLException, DMLException {
        guiAccessor.addGuiEditor(playerDatabaseObject.getUuid(), guiId);
    }
}