package com.dnamaster10.tcgui.objects.buttons;

import com.dnamaster10.tcgui.TraincartsGui;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class Ticket extends Button {
    String tcName;
    String displayName;
    public void giveToPlayer(Player p) {
        p.getInventory().addItem(item);
    }
    public Ticket(String tcName, String displayName, int price) {
        this.tcName = tcName;
        this.displayName = displayName;

        //Create item
        item = new ItemStack(Material.PAPER, 1);

        //Set data
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName(displayName);

        //Set type data
        NamespacedKey typeKey = new NamespacedKey(TraincartsGui.getPlugin(), "type");
        meta.getPersistentDataContainer().set(typeKey, PersistentDataType.STRING, "ticket");

        //Set tc ticket data
        NamespacedKey tcKey = new NamespacedKey(TraincartsGui.plugin, "tc_name");
        meta.getPersistentDataContainer().set(tcKey, PersistentDataType.STRING, tcName);

        //Set price data
        NamespacedKey priceKey = new NamespacedKey(TraincartsGui.plugin, "price");
        meta.getPersistentDataContainer().set(priceKey, PersistentDataType.INTEGER, price);

        item.setItemMeta(meta);
    }
}