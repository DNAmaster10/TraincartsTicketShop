package com.dnamaster10.traincartsticketshop.commands.commandhandlers.ticket;

import com.dnamaster10.traincartsticketshop.commands.commandhandlers.SyncCommandHandler;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.StringJoiner;

import static com.dnamaster10.traincartsticketshop.util.ButtonUtils.getButtonType;
import static com.dnamaster10.traincartsticketshop.objects.buttons.DataKeys.PURCHASE_MESSAGE;

public class TicketSetPurchaseMessageCommandHandler extends SyncCommandHandler {
    //Example command: /tshop ticket setPurchaseMessage <message>
    private Player player;
    private String displayText;
    private ItemStack ticket;

    @Override
    protected boolean checkSync(CommandSender sender, String[] args) {
        //Check that sender is a player
        if (!(sender instanceof Player)) {
            returnOnlyPlayersExecuteError(sender);
            return false;
        }
        player = (Player) sender;

        if (!player.hasPermission("traincartsticketshop.ticket.setpurchasemessage")) {
            returnInsufficientPermissionsError(player);
            return false;
        }

        //Check syntax
        if (args.length < 3) {
            returnMissingArgumentsError(sender, "/tshop ticket setPurchaseMessage <message>");
            return false;
        }

        //Build display text
        StringJoiner stringJoiner = new StringJoiner(" ");
        for (int i = 2; i < args.length; i++) stringJoiner.add(args[i]);
        displayText = ChatColor.translateAlternateColorCodes('&', stringJoiner.toString());

        if (displayText.length() > 500) {
            returnError(player, "Purchase messages cannot be more than 500 characters in length");
            return false;
        }
        if (displayText.isBlank()) {
            returnError(player, "Purchase messages cannot be blank");
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
        dataContainer.set(PURCHASE_MESSAGE, PersistentDataType.STRING, displayText);
        ticket.setItemMeta(meta);
        player.sendMessage(ChatColor.GREEN + "Held ticket's purchase message was set successfully");
    }
}
