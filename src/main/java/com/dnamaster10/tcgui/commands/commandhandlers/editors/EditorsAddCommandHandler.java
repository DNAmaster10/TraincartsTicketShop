package com.dnamaster10.tcgui.commands.commandhandlers.editors;

import com.dnamaster10.tcgui.commands.commandhandlers.CommandHandler;
import com.dnamaster10.tcgui.util.MojangApiAccessor;
import com.dnamaster10.tcgui.util.Players;
import com.dnamaster10.tcgui.util.database.GuiAccessor;
import com.dnamaster10.tcgui.util.database.PlayerAccessor;
import com.dnamaster10.tcgui.util.database.databaseobjects.PlayerDatabaseObject;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.sql.SQLException;

public class EditorsAddCommandHandler extends CommandHandler<SQLException> {
    //Command example: /tcgui editors add <player_name> <gui_name>
    //This is computed during the async check, so is stored here to be used later in the execute method.
    private PlayerDatabaseObject playerDatabaseObject;
    @Override
    protected boolean checkSync(CommandSender sender, String[] args) {
        //Check config
        if (!getPlugin().getConfig().getBoolean("AllowEditorAdd")) {
            returnError(sender, "Adding editors is disabled on this server");
            return false;
        }

        //If player check perms
        if (sender instanceof Player p) {
            if (!p.hasPermission("tcgui.editors.add")) {
                returnError(sender, "You do not have permission to perform that action");
                return false;
            }
        }

        //Check syntax
        if (args.length < 4) {
            returnError(sender, "Missing argument(s): /tcgui editors add <gui_name> <username>");
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
        //Check gui exists
        GuiAccessor guiAccessor = new GuiAccessor();
        if (!guiAccessor.checkGuiByName(args[3])) {
            returnGuiNotFoundError(sender, args[3]);
            return false;
        }

        //Check player is owner
        if (sender instanceof Player p) {
            if (!guiAccessor.checkGuiOwnershipByUuid(args[3], p.getUniqueId().toString())) {
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
        return true;
    }

    @Override
    protected void execute(CommandSender sender, String[] args) throws SQLException {
        GuiAccessor accessor = new GuiAccessor();
        int guiId = accessor.getGuiIdByName(args[3]);
        accessor.addGuiEditor(playerDatabaseObject.getUuid(), guiId);
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
            }
            catch (SQLException e) {
                getPlugin().reportSqlError(sender, e.toString());
            }
        });
    }
}