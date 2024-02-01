package com.dnamaster10.tcgui.commands.commandhandlers.ticket;

import com.dnamaster10.tcgui.commands.commandhandlers.ItemCommandHandler;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.StringJoiner;

public class TicketSetDisplayNameCommandHandler extends ItemCommandHandler {
    //Example command: /tcgui ticket rename <new_name>
    String colouredDisplayName;
    @Override
    protected boolean checkSync(CommandSender sender, String[] args) {
        //Check config
        if (!getPlugin().getConfig().getBoolean("AllowTicketSetDisplayName")) {
            returnError(sender, "Ticket renaming is disabled on this server");
            return false;
        }

        //Check that sender is a player
        if (!(sender instanceof Player p)) {
            returnError(sender, "Command must be executed by a player");
            return false;
        }
        else {
            if (!p.hasPermission("tcgui.ticket.setdisplayname")) {
                returnError(sender, "You do not have permission to perform that action");
                return false;
            }
        }

        //Check syntax
        if (args.length < 3) {
            returnError(sender, "Please enter a new name for the ticket");
            return false;
        }

        //Build display name
        StringJoiner stringJoiner = new StringJoiner(" ");
        for (int i = 2; i < args.length; i++) {
            stringJoiner.add(args[i]);
        }
        colouredDisplayName = ChatColor.translateAlternateColorCodes('&', stringJoiner.toString());
        String rawDisplayName = ChatColor.stripColor(colouredDisplayName);

        if (rawDisplayName.length() > 25) {
            returnError(sender, "Ticket names cannot be more than 25 characters in length");
            return false;
        }
        if (rawDisplayName.isBlank()) {
            returnError(sender, "Ticket names cannot be less than 1 character in length");
            return false;
        }
        if (colouredDisplayName.length() > 100) {
            returnError(sender, "Too many colours!");
        }

        //Now check that the player is holding a ticket
        ItemStack ticket = ((Player) sender).getInventory().getItemInMainHand();
        String buttonType = getButtonType(ticket);
        if (buttonType == null) {
            returnWrongItemError(sender, "ticket");
            return false;
        }

        return true;
    }

    @Override
    protected boolean checkAsync(CommandSender sender, String[] args) {
        return true;
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        //Get item
        ItemStack ticket = ((Player) sender).getInventory().getItemInMainHand();
        ItemMeta meta = ticket.getItemMeta();
        assert meta != null;
        meta.setDisplayName(colouredDisplayName);
        ticket.setItemMeta(meta);
        sender.sendMessage(ChatColor.GREEN + "Held ticket was renamed to \"" + colouredDisplayName + "\"");
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        if (!checkSync(sender, args)) {
            return;
        }
        execute(sender, args);
    }
}
