package com.dnamaster10.tcgui.commands.commandhandlers;

import com.dnamaster10.tcgui.TraincartsGui;
import com.dnamaster10.tcgui.objects.EditGui;
import com.dnamaster10.tcgui.util.GuiManager;
import com.dnamaster10.tcgui.util.database.GuiAccessor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class GuiEditCommandHandler extends CommandHandler<SQLException> {
    //Example command: /tcgui gui edit <gui_name>
    @Override
    boolean checkSync(CommandSender sender, String[] args) {
        //Check config
        if (!getPlugin().getConfig().getBoolean("AllowGuiEdit")) {
            returnError(sender, "Gui editing is disabled on this server");
            return false;
        }

        //Check sender is player and permissions
        if (!(sender instanceof Player p)) {
            returnError(sender, "Command must be executed by a player");
            return false;
        }
        else {
            if (!p.hasPermission("tcgui.gui.edit")) {
                returnError(sender, "You do not have permission to edit that gui");
                return false;
            }
        }

        //Check syntax
        if (args.length < 3)  {
            returnError(sender, "Please enter a gui name to edit");
            return false;
        }
        if (args.length > 3) {
            returnError(sender, "Invalid sub-command \"" + args[3] + "\"");
            return false;
        }

        return true;
    }

    @Override
    boolean checkAsync(CommandSender sender, String[] args) throws SQLException {
        GuiAccessor guiAccessor = new GuiAccessor();

        //Check that gui exists
        if (!guiAccessor.checkGuiByName(args[2])) {
            returnError(sender, "No gui with name \"" + args[2] + "\" exists");
            return false;
        }

        //Check that player is owner or editor of gui
        if (!guiAccessor.playerCanEdit(args[2], ((Player) sender).getUniqueId().toString())) {
            returnError(sender, "You do not have permission to edit that gui");
            return false;
        }
        return true;
    }

    @Override
    void execute(CommandSender sender, String[] args) throws SQLException {
        GuiManager guiManager = getPlugin().getGuiManager();

        //Create a new GUI
        EditGui gui = new EditGui(args[2]);

        //Open the gui
        gui.open((Player) sender);

        //Register the gui
        TraincartsGui.plugin.getGuiManager().registerNewEditGui(gui, (Player) sender);
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
                getPlugin().reportSqlError(sender, e.toString());
            }
        });
    }
}
