package com.dnamaster10.tcgui.commands.commandhandlers.ticket;

import com.dnamaster10.tcgui.commands.commandhandlers.ItemCommandHandler;
import com.dnamaster10.tcgui.util.Traincarts;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

import static com.dnamaster10.tcgui.objects.buttons.DataKeys.TC_TICKET_NAME;

public class TicketSetTraincartsTicket extends ItemCommandHandler {
    //Example command: /tcgui ticket setTraincartsTicket <traincarts ticket>
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
            if (!p.hasPermission("tcgui.ticket.settraincartsticket")) {
                returnError(sender, "You do not have permission to perform that action");
                return false;
            }
        }

        //Check syntax
        if (args.length < 3) {
            returnError(sender, "Missing argument(s): /tcgui ticket setTraincartsTicket <traincarts ticket>");
            return false;
        }
        if (args.length > 3) {
            returnError(sender, "Invalid sub-command \"" + args[3] + "\"");
            return false;
        }

        //Check traincarts ticket exists
        if (!Traincarts.checkTicket(args[2])) {
            returnError(sender, "Traincarts ticket \"" + args[2] + "\" does not exist");
            return false;
        }

        //Check player is holding a ticket
        ItemStack ticket = ((Player) sender).getInventory().getItemInMainHand();
        String buttonType = getButtonType(ticket);
        if (!Objects.equals(buttonType, "ticket")) {
            returnWrongItemError(sender, "ticket");
            return false;
        }

        return true;
    }

    @Override
    protected boolean checkAsync(CommandSender sender, String[] args) throws Exception {
        return true;
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        ItemStack ticket = ((Player) sender).getInventory().getItemInMainHand();
        ItemMeta meta = ticket.getItemMeta();
        assert meta != null;

        PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
        dataContainer.set(TC_TICKET_NAME, PersistentDataType.STRING, args[2]);
        ticket.setItemMeta(meta);

        sender.sendMessage(ChatColor.GREEN + "Traincarts ticket changed to \"" + args[2] + "\"");
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        if (!checkSync(sender, args)) {
            return;
        }
        execute(sender, args);
    }
}
