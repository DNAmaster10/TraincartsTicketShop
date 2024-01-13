package com.dnamaster10.tcgui.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.sql.SQLException;

public class GuiRenameCommandHandler extends CommandHandler {
    //Example command:
    // /tcgui gui rename old_name new_name

    @Override
    boolean checkSync(CommandSender sender, String[] args) {
        //This command can be run by the player as well as other interfaces.
        //We first check things which apply to both

        //Check config
        if (!getPlugin().getConfig().getBoolean("AllowGuiRename")) {
            returnError(sender, "Gui renaming is disabled on this server");
            return false;
        }

        //Check syntax
        if (args.length < 4) {
            //returnError(sender, "Missing argument(s): " + );
        }
        return true;
    }

    @Override
    boolean checkAsync(CommandSender sender, String[] args) throws SQLException {
        return false;
    }

    @Override
    void execute(CommandSender sender, String[] args) throws SQLException {

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
                getPlugin().reportSqlError(e.toString());
            }
        });
    }
}
