package com.dnamaster10.tcgui.commands.commandhandlers.linker;

import com.dnamaster10.tcgui.commands.commandhandlers.CommandHandler;
import com.dnamaster10.tcgui.objects.buttons.LinkerButton;
import com.dnamaster10.tcgui.util.database.GuiAccessor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class LinkerCreateCommandHandler extends CommandHandler<SQLException> {
    //Example command: /tcgui linker create <linked_gui_name> <display_name>
    @Override
    protected boolean checkSync(CommandSender sender, String[] args) {
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
            if (!p.hasPermission("tcgui.linker.create")) {
                returnError(sender, "You do not have permission to perform that action");
                return false;
            }
        }
        //Check syntax
        if (args.length < 4) {
            returnError(sender, "Missing arguments: /tcgui linker create <linked_gui_name> <display_name>");
            return false;
        }
        if (args.length > 4) {
            returnError(sender, "Invalid sub-command \"" + args[4] + "\"");
            return false;
        }
        if (!checkStringFormat(args[3])) {
            returnError(sender, "Linker names can only contain characters Aa - Zz, numbers, underscores and dashes");
        }

        return true;
    }

    @Override
    protected boolean checkAsync(CommandSender sender, String[] args) throws SQLException {
        GuiAccessor guiAccessor = new GuiAccessor();

        //Check that gui exists
        if (!guiAccessor.checkGuiByName(args[3])) {
            returnError(sender, "No gui with name \"" + args[3] + "\" exists");
            return false;
        }

        return true;
    }

    @Override
    protected void execute(CommandSender sender, String[] args) throws SQLException {
        //Get gui ID
        GuiAccessor accessor = new GuiAccessor();
        int guiId = accessor.getGuiIdByName(args[2]);
        LinkerButton button = new LinkerButton(guiId, args[3]);
        Player p = (Player) sender;
        p.getInventory().addItem(button.getItemStack());
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
