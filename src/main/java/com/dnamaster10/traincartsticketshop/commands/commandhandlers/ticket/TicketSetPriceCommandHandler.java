package com.dnamaster10.traincartsticketshop.commands.commandhandlers.ticket;

import com.dnamaster10.traincartsticketshop.commands.commandhandlers.SyncCommandHandler;
import com.dnamaster10.traincartsticketshop.util.Utilities;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import static com.dnamaster10.traincartsticketshop.TraincartsTicketShop.getPlugin;
import static com.dnamaster10.traincartsticketshop.objects.buttons.DataKeys.PRICE;
import static com.dnamaster10.traincartsticketshop.util.ButtonUtils.getButtonType;

public class TicketSetPriceCommandHandler extends SyncCommandHandler {
    //Example command: /tshop ticket setPrice <price>
    private Player player;
    private ItemStack ticket;

    @Override
    protected boolean checkSync(CommandSender sender, String[] args) {
        //Check sender is a player
        if (!(sender instanceof  Player)) {
            returnOnlyPlayersExecuteError(sender);
            return false;
        }
        player = (Player) sender;

        if (!player.hasPermission("traincartsticketshop.ticket.setprice")) {
            returnInsufficientPermissionsError(player);
            return false;
        }

        //Check config
        if (!getPlugin().getConfig().getBoolean("AllowCustomTicketPrices")) {
            returnError(player, "Custom ticket prices are disabled in the config.");
            return false;
        }

        //Check syntax
        if (args.length < 3) {
            returnMissingArgumentsError(player, "/tshop ticket setPrice <price>");
            return false;
        }
        if (args.length > 3) {
            returnInvalidSubCommandError(player, args[3]);
            return false;
        }
        if (!Utilities.isDouble(args[2])) {
            returnError(player, "Ticket prices must be numeric");
            return false;
        }

        double price = Double.parseDouble(args[2]);

        //Check price
        if (price < 0) {
            returnError(player, "Ticket prices must be positive");
            return false;
        }

        double minimum = getPlugin().getConfig().getDouble("MinTicketPrice");
        double maximum = getPlugin().getConfig().getDouble("MaxTicketPrice");

        if (price < minimum) {
            returnError(player, "Ticket prices cannot be less than " + minimum);
            return false;
        }
        if (price > maximum) {
            returnError(player, "Ticket prices cannot be more than " + maximum);
            return false;
        }

        //Check that player is holding a ticket
        ticket = player.getInventory().getItemInMainHand();
        String buttonType = getButtonType(ticket);
        if (buttonType == null || !buttonType.equals("ticket")) {
            returnWrongItemError(player, "ticket");
            return false;
        }

        return true;
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        ItemMeta meta = ticket.getItemMeta();
        assert meta != null;
        PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
        dataContainer.set(PRICE, PersistentDataType.DOUBLE, Double.parseDouble(args[2]));
        ticket.setItemMeta(meta);
        player.sendMessage(ChatColor.GREEN + "Held ticket's price was set successfully");
    }
}
