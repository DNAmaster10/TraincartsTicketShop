package com.dnamaster10.traincartsticketshop.commands.commandhandlers.ticket;

import com.dnamaster10.traincartsticketshop.commands.commandhandlers.SyncCommandHandler;
import com.dnamaster10.traincartsticketshop.objects.buttons.Ticket;
import com.dnamaster10.traincartsticketshop.util.Traincarts;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.StringJoiner;

import static com.dnamaster10.traincartsticketshop.TraincartsTicketShop.getPlugin;

public class TicketCreateCommandHandler extends SyncCommandHandler {
    //Example command: /tshop ticket create <tc_ticket_name> <display_name>
    private String colouredDisplayName;
    private Player player;
    @Override
    protected boolean checkSync(CommandSender sender, String[] args) {
        //Check sender is player and permissions
        if (!(sender instanceof Player)) {
            returnOnlyPlayersExecuteError(sender);
            return false;
        }
        player = (Player) sender;

        if (!player.hasPermission("traincartsticketshop.ticket.create")) {
            returnInsufficientPermissionsError(player);
            return false;
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
        colouredDisplayName = ChatColor.translateAlternateColorCodes('&', stringJoiner.toString());
        String rawDisplayName = ChatColor.stripColor(colouredDisplayName);

        if (rawDisplayName.isBlank()) {
            returnError(player, "Ticket names cannot be less than 1 character in length");
            return false;
        }
        if (rawDisplayName.length() > 25) {
            returnError(player, "Ticket names cannot be more than 25 characters in length");
            return false;
        }
        if (colouredDisplayName.length() > 100) {
            returnError(sender, "You've used too many colours in that display name");
            return false;
        }

        //Check traincarts ticket
        if (!Traincarts.checkTicket(args[2])) {
            returnError(player, "No traincarts ticket with the name \"" + args[2] + "\" exists");
            return false;
        }

        //If all checks pass return true
        return true;
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        //Create the ticket object
        Ticket ticket = new Ticket(args[2], colouredDisplayName);

        //Give the ticket to the player
        ItemStack ticketItem = ticket.getItemStack();
        player.getInventory().addItem(ticketItem);

        player.sendMessage(ChatColor.GREEN + "Ticket created");
    }
}
