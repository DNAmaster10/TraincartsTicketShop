package com.dnamaster10.tcgui.commands.commandhandlers.gui;

import com.dnamaster10.tcgui.commands.commandhandlers.CommandHandler;
import com.dnamaster10.tcgui.objects.guis.EditGui;
import com.dnamaster10.tcgui.util.database.GuiAccessor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class GuiEditCommandHandler extends CommandHandler {
    //Example command: /tcgui gui edit <gui_name>
    private GuiAccessor guiAccessor;
    @Override
    protected boolean checkSync(CommandSender sender, String[] args) {
        //Check config
        if (!getPlugin().getConfig().getBoolean("AllowGuiEdit")) {
            returnError(sender, "Gui editing is disabled on this server");
            return false;
        }

        //Check sender is player and permissions
        if (!(sender instanceof Player)) {
            returnError(sender, "Command must be executed by a player");
            return false;
        }
        if (!sender.hasPermission("tcgui.gui.edit") && !sender.hasPermission("tcgui.admin.gui.edit")) {
            returnError(sender, "You do not have permission to perform that action");
            return false;
        }

        //Check syntax
        if (args.length < 3)  {
            returnError(sender, "Missing argument(s): /tcgui gui edit <gui name>");
            return false;
        }
        if (args.length > 3) {
            returnError(sender, "Invalid sub-command \"" + args[3] + "\"");
            return false;
        }
        if (!checkGuiNameSyntax(args[2])) {
            returnGuiNotFoundError(sender, args[2]);
        }

        return true;
    }

    @Override
    protected boolean checkAsync(CommandSender sender, String[] args) throws SQLException {
        guiAccessor = new GuiAccessor();

        //Check that gui exists
        if (!guiAccessor.checkGuiByName(args[2])) {
            returnGuiNotFoundError(sender, args[2]);
            return false;
        }

        //Check that player is owner or editor of gui
        Player p = (Player) sender;
        if (!p.hasPermission("tcgui.admin.gui.edit")) {
            if (!guiAccessor.playerCanEdit(args[2], p.getUniqueId().toString())) {
                returnError(sender, "You do not have permission to edit that gui. Request that the owner adds you as an editor before making any changes");
                return false;
            }
        }
        return true;
    }

    @Override
    protected void execute(CommandSender sender, String[] args) throws SQLException {
        //Create a new GUI
        EditGui gui = new EditGui(args[2], (Player) sender);

        //Open the gui
        gui.open();

        //Remove all previous guis registered to player as this is a new session
        getPlugin().getGuiManager().clearGuis((Player) sender);

        //Register the gui
        getPlugin().getGuiManager().addGui((Player) sender, gui);
    }

}
