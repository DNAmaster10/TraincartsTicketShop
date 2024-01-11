package com.dnamaster10.tcgui.objects;

import com.dnamaster10.tcgui.TraincartsGui;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class Ticket {
    String tcName;
    String displayName;
    ItemStack item;
    public void giveToPlayer(Player p) {
        p.getInventory().addItem(item);
    }
    public String getTcTicketName() {
        return tcName;
    }
    public Ticket(String tcName, String displayName) {
        this.tcName = tcName;
        this.displayName = displayName;

        //Create item
        item = new ItemStack(Material.PAPER, 1);

        //Store traincart data
        ItemMeta meta = item.getItemMeta();
        NamespacedKey key = new NamespacedKey(TraincartsGui.plugin, "ticket_name");
        assert meta != null;
        meta.setDisplayName(displayName);
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, tcName);
        item.setItemMeta(meta);
    }
}
