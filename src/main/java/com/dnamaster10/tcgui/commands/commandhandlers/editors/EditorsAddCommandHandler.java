package com.dnamaster10.tcgui.commands.commandhandlers.editors;

import com.dnamaster10.tcgui.commands.commandhandlers.CommandHandler;
import com.dnamaster10.tcgui.util.MojangApiAccessor;
import com.dnamaster10.tcgui.util.database.GuiAccessor;
import com.dnamaster10.tcgui.util.database.PlayerAccessor;
import com.dnamaster10.tcgui.util.database.databaseobjects.PlayerDatabaseObject;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.sql.SQLException;

public class EditorsAddCommandHandler extends CommandHandler<SQLException> {
    //Command example: /tcgui editors add <gui_name> <editor_name>
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
        if (!checkGuiNameSyntax(args[2])) {
            returnGuiNotFoundError(sender, args[2]);
            return false;
        }

        return true;
    }

    @Override
    protected boolean checkAsync(CommandSender sender, String[] args) throws SQLException {
        //Check gui exists
        GuiAccessor guiAccessor = new GuiAccessor();
        if (!guiAccessor.checkGuiByName(args[2])) {
            returnGuiNotFoundError(sender, args[2]);
            return false;
        }

        //Check player is owner
        if (sender instanceof Player p) {
            if (!guiAccessor.checkGuiOwnershipByUuid(args[2], p.getUniqueId().toString())) {
                returnError(sender, "You do not own that gui");
                return false;
            }
        }

        //Finally, we need to check that the entered username is an actual username, and if so, get their UUID.
        //This requires use of the Mojang API if the player has never connected before
        //First, check if player is online
        for (Player p : getPlugin().getServer().getOnlinePlayers()) {
            if (p.getDisplayName().equalsIgnoreCase(args[3])) {
                playerDatabaseObject = new PlayerDatabaseObject(p.getDisplayName(), p.getUniqueId().toString());
                return true;
            }
        }
        //If not, check if they already exist in the database
        PlayerAccessor playerAccessor = new PlayerAccessor();
        if (playerAccessor.checkPlayerByUsername(args[3])) {
            playerDatabaseObject = playerAccessor.getPlayerByUsername(args[3]);
        }
        else {
            //Player was not found in database - Check with Mojang API
            MojangApiAccessor apiAccessor = new MojangApiAccessor();
            try {
                String[] playerApiString = apiAccessor.getPlayerFromUsername(args[3]);
                if (playerApiString == null || playerApiString.length < 2) {
                    //Either the API is down, or the player does not exist.
                    returnError(sender, "No player with the username \"" + args[3] + "\" could be found");
                    return false;
                }

                //Add the player to the database
                playerAccessor.updatePlayer(playerApiString[0], playerApiString[1]);
                playerDatabaseObject = new PlayerDatabaseObject(playerApiString[0], playerApiString[1]);
            } catch (IOException e) {
                //Either the API is down, or the player does not exist.
                returnError(sender, "No player with the username \"" + args[3] + "\" could be found");
                return false;
            }
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