package com.dnamaster10.tcgui.commands.commandhandlers.editor;

import com.dnamaster10.tcgui.commands.commandhandlers.CommandHandler;
import com.dnamaster10.tcgui.util.Players;
import com.dnamaster10.tcgui.util.database.GuiAccessor;
import com.dnamaster10.tcgui.util.database.databaseobjects.PlayerDatabaseObject;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class EditorRemoveAllCommandHandler extends CommandHandler<SQLException> {
    //Example command: /tcgui editor removeAll <gui name>
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
            if (!p.hasPermission("tcgui.editor.removeall") && !p.hasPermission("tcgui.admin.editor.removeall")) {
                returnError(sender, "You do not have permission to perform that action");
                return false;
            }
        }

        //Check syntax
        if (args.length < 3) {
            returnError(sender, "Missing argument(s): /tcgui editor removeAll <gui name>");
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
    protected boolean checkAsync(CommandSender sender, String[] args) throws SQLException {
        //Check gui exists
        guiAccessor = new GuiAccessor();
        if (!guiAccessor.checkGuiByName(args[2])) {
            returnGuiNotFoundError(sender, args[2]);
            return false;
        }

        //Check player is owner or isn't admin
        if (sender instanceof Player p) {
            if (!p.hasPermission("tcgui.admin.editor.removeall")) {
                if (!guiAccessor.checkGuiOwnershipByUuid(args[2], p.getUniqueId().toString())) {
                    returnError(sender, "You do not own that gui");
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    protected void execute(CommandSender sender, String[] args) throws SQLException {
        int guiId = guiAccessor.getGuiIdByName(args[2]);
        guiAccessor.removeAllGuiEditors(guiId);
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        if (!checkSync(sender, args)) {
            return;
        }
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            try {
                if (!checkSync(sender, args)) {
                    return;
                }
                execute(sender, args);
            } catch (SQLException e) {
                getPlugin().reportSqlError(sender, e);
            }
        });
    }
}
