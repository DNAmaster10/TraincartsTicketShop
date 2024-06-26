package com.dnamaster10.traincartsticketshop.objects.buttons;

import com.dnamaster10.traincartsticketshop.util.newdatabase.databaseobjects.TicketDatabaseObject;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import static com.dnamaster10.traincartsticketshop.objects.buttons.DataKeys.*;

public class Ticket extends Button {
    private final String tcTicketName;
    private final String displayName;
    private final String purchaseMessage;
    public TicketDatabaseObject getAsDatabaseObject(int slot) {
        String rawDisplayName = ChatColor.stripColor(displayName);
        return new TicketDatabaseObject(slot, tcTicketName, displayName, rawDisplayName, purchaseMessage);
    }
    @Override
    public ItemStack getItemStack() {
        ItemStack item = new ItemStack(Material.PAPER, 1);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName(displayName);

        PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
        dataContainer.set(BUTTON_TYPE, PersistentDataType.STRING, "ticket");
        dataContainer.set(TC_TICKET_NAME, PersistentDataType.STRING, this.tcTicketName);
        if (purchaseMessage != null) {
            dataContainer.set(PURCHASE_MESSAGE, PersistentDataType.STRING, this.purchaseMessage);
        }

        item.setItemMeta(meta);
        return item;
    }
    public Ticket(String tcName, String displayName, String purchaseMessage) {
        this.tcTicketName = tcName;
        this.displayName = displayName;
        this.purchaseMessage = purchaseMessage;
    }
}
