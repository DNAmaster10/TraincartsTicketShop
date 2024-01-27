package com.dnamaster10.tcgui.commands.commandhandlers.ticket;

import com.dnamaster10.tcgui.commands.commandhandlers.CommandHandler;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.sql.SQLException;
import java.util.StringJoiner;

public class TicketRenameCommandHandler extends CommandHandler<SQLException> {
    //Example command: /tcgui ticket rename <new_name>
    String displayName;
    @Override
    protected boolean checkSync(CommandSender sender, String[] args) {
        //Check config
        if (!getPlugin().getConfig().getBoolean("AllowTicketRename")) {
            returnError(sender, "Ticket renaming is disabled on this server");
            return false;
        }

        //Check that sender is a player
        if (!(sender instanceof Player)) {
            returnError(sender, "Command must be executed by a player");
            return false;
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
        displayName = stringJoiner.toString();

        if (displayName.length() > 20) {
            returnError(sender, "Ticket names cannot be more than 20 characters in length");
            return false;
        }
        if (displayName.isEmpty() || displayName.isBlank()) {
            returnError(sender, "Ticket names cannot be less than 1 character in length");
            return false;
        }

        //Check permissions
        if (!sender.hasPermission("tcgui.ticket.rename")) {
            returnError(sender, "You do not have permission to perform that action");
            return false;
        }

        //Now check that the player is holding a ticket
        ItemStack ticket = ((Player) sender).getInventory().getItemInMainHand();
        if (!ticket.hasItemMeta()) {
            returnError(sender, "You must be holding a ticket item in your main hand");
            return false;
        }
        ItemMeta meta = ticket.getItemMeta();
        NamespacedKey key = new NamespacedKey(getPlugin(), "type");
        assert meta != null;
        if (!meta.getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
            returnError(sender, "You must be holding a ticket item in your main hand");
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
        meta.setDisplayName(displayName);
        ticket.setItemMeta(meta);
        sender.sendMessage(ChatColor.GREEN + "Held ticket was renamed to \"" + displayName + "\"");
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        if (!checkSync(sender, args)) {
            return;
        }
        execute(sender, args);
    }
}
