package com.dnamaster10.traincartsticketshop.commands.commandhandlers.gui;

import com.dnamaster10.traincartsticketshop.commands.commandhandlers.CommandHandler;
import com.dnamaster10.traincartsticketshop.objects.guis.multipageguis.ShopGui;
import com.dnamaster10.traincartsticketshop.util.Session;
import com.dnamaster10.traincartsticketshop.util.database.GuiAccessor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class GuiOpenCommandHandler extends CommandHandler {
    //Example command: /traincartsticketshop gui open <gui_name>
    private GuiAccessor guiAccessor;
    private Player player;
    @Override
    protected boolean checkSync(CommandSender sender, String[] args) {
        //Check config
        if (!getPlugin().getConfig().getBoolean("AllowGuiOpen")) {
            returnError(sender, "Opening guis is disabled on this server");
            return false;
        }

        //Check sender is player
        if (!(sender instanceof Player p)) {
            returnError(sender, "Command must be executed by a player");
            return false;
        }
        else {
            //Check permissions
            player = p;
            if (!player.hasPermission("traincartsticketshop.gui.open")) {
                returnError(sender, "You do not have permission to perform that action");
                return false;
            }
        }

        //Check syntax
        if (args.length < 3) {
            returnError(sender, "Please enter a gui name");
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
        guiAccessor = new GuiAccessor();

        //Check that gui exists
        if (!guiAccessor.checkGuiByName(args[2])) {
            returnGuiNotFoundError(player, args[2]);
            return false;
        }
        return true;
    }

    @Override
    protected void execute(CommandSender sender, String[] args) throws SQLException {
        //Get the gui id
        int guiId = guiAccessor.getGuiIdByName(args[2]);

        //Create a new gui
        ShopGui gui = new ShopGui(guiId, player);

        //Create a new gui session
        Session session = getPlugin().getGuiManager().getNewSession(player);

        //Register the new gui
        session.addGui(gui);

        //Open the new gui
        gui.open();
    }
}
