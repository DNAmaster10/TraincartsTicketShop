package com.dnamaster10.tcgui.objects.buttons;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import static com.dnamaster10.tcgui.TraincartsGui.getPlugin;

public class Ticket extends Button {
    public void giveToPlayer(Player p) {
        p.getInventory().addItem(item);
    }
    public Ticket(String tcName, String displayName, int price) {

        //Create item
        item = new ItemStack(Material.PAPER, 1);

        //Set data
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName(displayName);

        PersistentDataContainer dataContainer = meta.getPersistentDataContainer();

        //Set type data
        NamespacedKey typeKey = new NamespacedKey(getPlugin(), "button_type");
        dataContainer.set(typeKey, PersistentDataType.STRING, "ticket");

        //Set tc ticket data
        NamespacedKey tcKey = new NamespacedKey(getPlugin(), "tc_name");
        dataContainer.set(tcKey, PersistentDataType.STRING, tcName);

        //Set price data
        NamespacedKey priceKey = new NamespacedKey(getPlugin(), "price");
        dataContainer.set(priceKey, PersistentDataType.INTEGER, price);

        item.setItemMeta(meta);
    }
}
