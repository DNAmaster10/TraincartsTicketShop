package com.dnamaster10.tcgui.objects.buttons;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import static com.dnamaster10.tcgui.TraincartsGui.getPlugin;
import static com.dnamaster10.tcgui.objects.buttons.DataKeys.*;

public class Ticket extends Button {
    private final String tcTicketName;
    private final String displayName;
    public String getTcTicketName() {
        return this.tcTicketName;
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

        item.setItemMeta(meta);
        return item;
    }
    public Ticket(String tcName, String displayName) {
        this.tcTicketName = tcName;
        this.displayName = displayName;
    }
}
