package com.dnamaster10.traincartsticketshop.util;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import static com.dnamaster10.traincartsticketshop.objects.buttons.DataKeys.PURCHASE_MESSAGE;
import static com.dnamaster10.traincartsticketshop.objects.buttons.DataKeys.TC_TICKET_NAME;

public class GuiUtils {
    public static void handleTicketItemPurchase(ItemStack ticketItem, Player player) {
        ItemMeta meta = ticketItem.getItemMeta();
        if (meta == null) {
            player.sendMessage(ChatColor.RED + "Ticket info is broken, failed to purchase ticket");
            return;
        }
        PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
        if (!dataContainer.has(TC_TICKET_NAME, PersistentDataType.STRING)) {
            player.sendMessage(ChatColor.RED + "Ticket info is broken, failed to purchase ticket");
            return;
        }
        String tcName = dataContainer.get(TC_TICKET_NAME, PersistentDataType.STRING);
        if (tcName == null || !Traincarts.checkTicket(tcName)) {
            player.sendMessage(ChatColor.RED + "Traincarts ticket \"" + tcName + "\" is invalid or has been deleted");
            return;
        }
        String purchaseMessage = null;
        if (dataContainer.has(PURCHASE_MESSAGE, PersistentDataType.STRING)) {
            purchaseMessage = dataContainer.get(PURCHASE_MESSAGE, PersistentDataType.STRING);
            if (purchaseMessage != null && purchaseMessage.isBlank()) purchaseMessage = null;
        }

        //Handle purchase
        Traincarts.giveTicketItem(tcName, player);
        player.sendMessage(ChatColor.GREEN + "You purchased a ticket!");
        if (purchaseMessage != null) {
            player.sendMessage("");
            player.sendMessage(ChatColor.AQUA + purchaseMessage);
            player.sendMessage("");
        }

    }
}
