package com.dnamaster10.tcgui.commands.commandhandlers.shop;

import com.dnamaster10.tcgui.commands.commandhandlers.CommandHandler;
import com.dnamaster10.tcgui.objects.guis.ShopGui;
import com.dnamaster10.tcgui.util.database.GuiAccessor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class ShopOpenCommandHandler extends CommandHandler<SQLException> {
    //Example command: /tcgui shop open <gui_name>
    private GuiAccessor guiAccessor;
    @Override
    protected boolean checkSync(CommandSender sender, String[] args) {
        //Check config
        if (!getPlugin().getConfig().getBoolean("AllowOpenShops")) {
            returnError(sender, "Opening shops is disabled on this server");
            return false;
        }

        //Check sender is player
        if (!(sender instanceof Player p)) {
            returnError(sender, "Command must be executed by a player");
            return false;
        }
        else {
            //Check permissions
            if (!p.hasPermission("tcgui.shop.open")) {
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
            returnGuiNotFoundError(sender, args[2]);
            return false;
        }
        return true;
    }

    @Override
    protected void execute(CommandSender sender, String[] args) throws SQLException {
        //Create a new gui
        ShopGui gui = new ShopGui(args[2], (Player) sender);

        //Open the gui
        gui.open();

        //Remove the old guis attached to this player
        getPlugin().getGuiManager().clearGuis((Player) sender);

        //Register the gui
        getPlugin().getGuiManager().addGui((Player) sender, gui);
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
                getPlugin().reportSqlError(sender, e);
            }
        });
    }
}
