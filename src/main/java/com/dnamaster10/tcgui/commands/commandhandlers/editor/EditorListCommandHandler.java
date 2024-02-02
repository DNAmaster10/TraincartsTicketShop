package com.dnamaster10.tcgui.commands.commandhandlers.editor;

import com.dnamaster10.tcgui.commands.commandhandlers.CommandHandler;
import com.dnamaster10.tcgui.util.database.GuiAccessor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EditorListCommandHandler extends CommandHandler<SQLException> {
    //Example command: /tcgui gui editor list <gui_name>
    private GuiAccessor guiAccessor;
    @Override
    protected boolean checkSync(CommandSender sender, String[] args) {
        //Check syntax
        if (args.length > 4) {
            returnError(sender, "Unrecognised sub-command \"" + args[4] + "\"");
            return false;
        }
        if (args.length < 4) {
            returnError(sender, "Please enter a gui name");
            return false;
        }


        //Check permissions
        if (sender instanceof Player p) {
            if (!p.hasPermission("tcgui.editor.list")) {
                returnError(sender, "You do not have permission to perform that action");
                return false;
            }
        }

        return true;
    }

    @Override
    protected boolean checkAsync(CommandSender sender, String[] args) throws SQLException {
        //Check that gui exists
        guiAccessor = new GuiAccessor();
        if (!guiAccessor.checkGuiByName(args[3])) {
            returnError(sender, "No gui with name \"" + args[3] + "\" exists");
            return false;
        }

        //If sender is a player
        if (sender instanceof Player p) {
            //If players aren't able to view editor for other people's guis
            if (!getPlugin().getConfig().getBoolean("AllowListGuiEditorsOtherOwners")) {
                //If player isn't the owner or editor
                if (guiAccessor.playerCanEdit(args[3], p.getUniqueId().toString())) {
                    returnError(sender, "You do not have permission to view the editor for that gui");
                    return false;
                }
            }
        }
        int guiId = guiAccessor.getGuiIdByName(args[3]);

        //Get players UUIDs
        List<String> uuids = new ArrayList<>();

        return true;
    }

    @Override
    protected void execute(CommandSender sender, String[] args) throws SQLException {

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
            } catch(SQLException e) {
                getPlugin().reportSqlError(sender, e);
            }
        });
    }
}
