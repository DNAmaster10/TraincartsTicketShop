package com.dnamaster10.traincartsticketshop.commands.commandhandlers.ticket;

import com.dnamaster10.traincartsticketshop.commands.commandhandlers.ItemCommandHandler;
import com.dnamaster10.traincartsticketshop.util.Traincarts;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

import static com.dnamaster10.traincartsticketshop.objects.buttons.DataKeys.TC_TICKET_NAME;

public class TicketSetTraincartsTicket extends ItemCommandHandler {
    //Example command: /traincartsticketshop ticket setTraincartsTicket <traincarts ticket>
    Player player;
    ItemStack ticket;
    @Override
    protected boolean checkSync(CommandSender sender, String[] args) {
        //Check config
        if (!getPlugin().getConfig().getBoolean("AllowTicketSetTraincartsTicket")) {
            returnError(sender, "Setting the Traincarts ticket is disabled on this server");
            return false;
        }

        //Check permissions and that sender is player
        if (!(sender instanceof Player p)) {
            returnError(sender, "Command must be executed by a player");
            return false;
        }
        else {
            player = p;
            if (!player.hasPermission("traincartsticketshop.ticket.settraincartsticket")) {
                returnError(player, "You do not have permission to perform that action");
                return false;
            }
        }

        //Check syntax
        if (args.length < 3) {
            returnError(player, "Missing argument(s): /traincartsticketshop ticket setTraincartsTicket <traincarts ticket>");
            return false;
        }
        if (args.length > 3) {
            returnError(player, "Invalid sub-command \"" + args[3] + "\"");
            return false;
        }

        //Check Traincarts ticket exists
        if (!Traincarts.checkTicket(args[2])) {
            returnError(player, "Traincarts ticket \"" + args[2] + "\" does not exist");
            return false;
        }

        //Check player is holding a ticket
        ticket = player.getInventory().getItemInMainHand();
        String buttonType = getButtonType(ticket);
        if (buttonType == null || !buttonType.equals("ticket")) {
            returnWrongItemError(player, "ticket");
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
        ItemMeta meta = ticket.getItemMeta();
        assert meta != null;

        PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
        dataContainer.set(TC_TICKET_NAME, PersistentDataType.STRING, args[2]);
        ticket.setItemMeta(meta);

        player.sendMessage(ChatColor.GREEN + "Traincarts ticket changed to \"" + args[2] + "\"");
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        if (!checkSync(sender, args)) {
            return;
        }
        execute(sender, args);
    }
}