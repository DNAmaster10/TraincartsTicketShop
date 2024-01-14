package com.dnamaster10.tcgui.commands.commandhandlers;

import com.dnamaster10.tcgui.objects.Ticket;
import com.dnamaster10.tcgui.util.Traincarts;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class TicketCreateCommandHandler extends CommandHandler<SQLException> {
    @Override
    boolean checkSync(CommandSender sender, String[] args) {
        //Synchronous checks (Syntax etc.)
        //Check config
        if (!getPlugin().getConfig().getBoolean("AllowTicketCreate")) {
            returnError(sender, "Ticket creation is disabled on this server");
            return false;
        }

        //Check that sender is a player
        if (!(sender instanceof Player)) {
            returnError(sender, "Command must be executed by a player");
            return false;
        }

        //Check syntax
        if (args.length < 4) {
            returnError(sender, "Missing argument(s): /tcgui ticket create <tc_ticket_name> <display_name>");
            return false;
        }
        if (args.length > 4) {
            returnError(sender, "Invalid sub-command \"" + args[4] + "\"");
            return false;
        }
        if (!checkStringFormat(args[3])) {
            returnError(sender, "Ticket names can only contains characters Aa - Zz, numbers, underscores and dashes.");
            return false;
        }

        //Check permissions
        if (!sender.hasPermission("tcgui.ticket.create")) {
            returnError(sender, "You do not have permission to perform that action");
            return false;
        }

        //If all checks pass return true
        return true;
    }
    @Override
    boolean checkAsync(CommandSender sender, String[] args) throws SQLException {
        //Check that ticket exits in traincarts.
        //Although not required to do async now, async is used in case traincarts switches to storing tickets in a database
        if (!Traincarts.checkTicket(args[2])) {
            returnError(sender, "No traincarts ticket with name \"" + args[2] + "\" exists");
            return false;
        }
        return true;
    }

    @Override
    void execute(CommandSender sender, String[] args) throws SQLException {
        Ticket ticket = new Ticket(args[2], args[3], 0);
        ticket.giveToPlayer((Player) sender);
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
