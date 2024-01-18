package com.dnamaster10.tcgui.commands.commandhandlers;

import com.dnamaster10.tcgui.TraincartsGui;
import com.dnamaster10.tcgui.util.ButtonBuilder;
import com.dnamaster10.tcgui.util.database.GuiAccessor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;

public class GuiLinkerCreateCommandHandler extends CommandHandler<SQLException> {
    //Example command: /tcgui gui linker create
    @Override
    boolean checkSync(CommandSender sender, String[] args) {
        //Check config
        if (!getPlugin().getConfig().getBoolean("AllowLinkerCreate")) {
            returnError(sender, "Linker creation is disabled on this server");
            return false;
        }

        //Check sender is player and permissions
        if (!(sender instanceof Player p)) {
            returnError(sender, "Command must be executed by a player");
            return false;
        }
        else {
            if (!p.hasPermission("tcgui.gui.linker.create")) {
                returnError(sender, "You do not have permission to perform that action");
                return false;
            }
        }

        //Check syntax
        if (args.length < 4) {
            returnError(sender, "Please enter a gui name");
            return false;
        }
        if (args.length > 4) {
            returnError(sender, "Invalid sub-command \"" + args[4] + "\"");
            return false;
        }

        return true;
    }

    @Override
    boolean checkAsync(CommandSender sender, String[] args) throws SQLException {
        GuiAccessor guiAccessor = new GuiAccessor();

        //Check that gui exists
        if (!guiAccessor.checkGuiByName(args[3])) {
            returnError(sender, "No gui with name \"" + args[3] + "\" exists");
            return false;
        }

        return true;
    }

    @Override
    void execute(CommandSender sender, String[] args) throws SQLException {
        ButtonBuilder builder = new ButtonBuilder();
        ItemStack button = builder.getLinkerButton(args[3]);
        Player p = (Player) sender;
        p.getInventory().addItem(button);
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
