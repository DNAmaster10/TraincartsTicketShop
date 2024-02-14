package com.dnamaster10.traincartsticketshop.commands.commandhandlers.ticket;

import com.dnamaster10.traincartsticketshop.commands.commandhandlers.AsyncCommandHandler;
import com.dnamaster10.traincartsticketshop.commands.commandhandlers.CommandHandler;
import com.dnamaster10.traincartsticketshop.objects.buttons.Ticket;
import com.dnamaster10.traincartsticketshop.util.Traincarts;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.StringJoiner;

public class TicketCreateCommandHandler extends AsyncCommandHandler {
    //Example command: /traincartsticketshop ticket create <tc_ticket_name> <display_name>
    private String displayName;
    private Player player;
    @Override
    protected boolean checkSync(CommandSender sender, String[] args) {
        //Synchronous checks (Syntax etc.)
        //Check config
        if (!getPlugin().getConfig().getBoolean("AllowTicketCreate")) {
            returnError(sender, "Ticket creation is disabled on this server");
            return false;
        }

        //Check sender is player and permissions
        if (!(sender instanceof Player p)) {
            returnOnlyPlayersExecuteError(sender);
            return false;
        }
        else {
            player = p;
            if (!player.hasPermission("traincartsticketshop.ticket.create")) {
                returnInsufficientPermissionsError(player);
                return false;
            }
        }

        //Check syntax
        if (args.length < 4) {
            returnMissingArgumentsError(player, "/tshop ticket create <tc ticket name> <display name>");
            return false;
        }

        StringJoiner stringJoiner = new StringJoiner(" ");
        for (int i = 3; i < args.length; i++) {
            stringJoiner.add(args[i]);
        }
        displayName = ChatColor.translateAlternateColorCodes('&', stringJoiner.toString());
        String rawDisplayName = ChatColor.stripColor(displayName);

        if (rawDisplayName.isBlank()) {
            returnError(player, "Ticket names cannot be less than 1 character in length");
            return false;
        }
        if (rawDisplayName.length() > 25) {
            returnError(player, "Ticket names cannot be more than 25 characters in length");
            return false;
        }
        if (displayName.length() > 100) {
            returnError(sender, "You've used too many colours in that display name");
            return false;
        }

        //If all checks pass return true
        return true;
    }
    @Override
    protected boolean checkAsync(CommandSender sender, String[] args) throws SQLException {
        //Check that ticket exits in traincarts.
        //Although not required to do async now, async is used in case traincarts switches to storing tickets in a database
        if (!Traincarts.checkTicket(args[2])) {
            returnError(player, "No traincarts ticket with the name \"" + args[2] + "\" exists");
            return false;
        }
        return true;
    }

    @Override
    protected void execute(CommandSender sender, String[] args) throws SQLException {
        //Create the ticket object
        Ticket ticket = new Ticket(args[2], displayName);

        //Give the ticket to the player
        ItemStack ticketItem = ticket.getItemStack();
        player.getInventory().addItem(ticketItem);
    }
}
