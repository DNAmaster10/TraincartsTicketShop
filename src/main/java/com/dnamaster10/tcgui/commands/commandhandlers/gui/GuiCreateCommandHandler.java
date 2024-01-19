package com.dnamaster10.tcgui.commands.commandhandlers.gui;

import com.dnamaster10.tcgui.commands.commandhandlers.CommandHandler;
import com.dnamaster10.tcgui.util.database.GuiAccessor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class GuiCreateCommandHandler extends CommandHandler<SQLException> {

    @Override
    protected boolean checkSync(CommandSender sender, String[] args) {
        //Synchronous checks (Syntax etc.)
        //Check config
        if (!getPlugin().getConfig().getBoolean("AllowGuiCreate")) {
            returnError(sender, "Gui creation is disabled on this server");
            return false;
        }

        //Check sender is player
        if (!(sender instanceof Player)) {
            returnError(sender, "Command must be executed by a player");
            return false;
        }

        //Check syntax
        if (args.length < 3) {
            returnError(sender, "Please enter a valid gui name");
            return false;
        }
        if (args.length > 3) {
            returnError(sender, "Invalid sub-command \"" + args[3] + "\"");
            return false;
        }
        if (args[2].length() > 100) {
            returnError(sender, "Gui names cannot be more than 100 characters in length");
            return false;
        }
        if (!checkStringFormat(args[2])) {
            returnError(sender, "Gui names can only contain letters Aa to Zz, numbers, underscores and dashes");
            return false;
        }

        //Check permissions
        if (!sender.hasPermission("tcgui.gui.create")) {
            returnError(sender, "You do not have permission to perform that action");
            return false;
        }

        //If all checks have passed return true
        return true;
    }

    @Override
    protected boolean checkAsync(CommandSender sender, String[] args) throws SQLException {
        //Asynchronous checks (Database etc.)
        //Method must be run from an already asynchronous method in order to be async
        Player p = (Player) sender;
        String guiName = args[2];
        GuiAccessor guiAccessor = new GuiAccessor();
        //Check gui doesn't already exist
        if (guiAccessor.checkGuiByName(guiName)) {
            returnError(p, "A gui with the name \"" + guiName + "\" already exists");
            return false;
        }
        return true;
    }

    @Override
    protected void execute(CommandSender sender, String[] args) throws SQLException {
        //Runs the command
        GuiAccessor guiAccessor = new GuiAccessor();
        guiAccessor.addGui(args[2], ((Player) sender).getUniqueId().toString());
        sender.sendMessage(ChatColor.GREEN + "A gui with name \"" + args[2] + "\" was created");
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
