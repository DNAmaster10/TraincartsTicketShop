package com.dnamaster10.tcgui.commands.commandhandlers.gui;

import com.dnamaster10.tcgui.commands.commandhandlers.CommandHandler;
import com.dnamaster10.tcgui.util.database.GuiAccessor;
import com.dnamaster10.tcgui.util.database.LinkerAccessor;
import com.dnamaster10.tcgui.util.database.TicketAccessor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class GuiDeleteCommandHandler extends CommandHandler<SQLException> {
    //Command example: /tcgui gui delete <gui_name>
    @Override
    protected boolean checkSync(CommandSender sender, String[] args) {
        //Synchronous checks
        //Check config
        if (!getPlugin().getConfig().getBoolean("AllowGuiDelete")) {
            returnError(sender, "Gui deletion is disabled on this server");
            return false;
        }

        //Check permissions if player
        if (sender instanceof Player p) {
            if (!p.hasPermission("tcgui.gui.delete")) {
                returnError(sender, "You do not have permission to perform that action");
                return false;
            }
        }

        //Check syntax
        if (args.length > 3) {
            returnError(sender, "Unrecognised sub-command \"" + args[3] + "\"");
            return false;
        }
        if (args.length < 3) {
            returnError(sender, "Please enter a gui name");
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
        //Check that GUI exists
        GuiAccessor guiAccessor = new GuiAccessor();
        if (!guiAccessor.checkGuiByName(args[2])) {
            returnGuiNotFoundError(sender, args[2]);
            return false;
        }

        //If sender is player, check they are owner
        if (sender instanceof Player p) {
            if (!guiAccessor.checkGuiOwnershipByUuid(args[2], p.getUniqueId().toString())) {
                returnError(sender, "You must be the owner of the gui in order to delete it");
                return false;
            }
        }
        return false;
    }

    @Override
    protected void execute(CommandSender sender, String[] args) throws SQLException {
        //Delete the gui
        GuiAccessor guiAccessor = new GuiAccessor();
        TicketAccessor ticketAccessor = new TicketAccessor();
        LinkerAccessor linkerAccessor = new LinkerAccessor();

        //Get gui id
        int id = guiAccessor.getGuiIdByName(args[2]);

        //Delete tickets
        ticketAccessor.deleteTicketsByGuid(id);

        //Delete linkers
        linkerAccessor.deleteLinkersByGuiId(id);

        //Delete gui
        guiAccessor.deleteGuiById(id);
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
