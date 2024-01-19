package com.dnamaster10.tcgui.objects.buttons;

import com.dnamaster10.tcgui.TraincartsGui;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.sql.SQLException;

public class LinkerButton extends Button {
    public void giveToPlayer(Player p) {
        p.getInventory().addItem(item);
    }
    public LinkerButton (int linkedGuiId, String displayName) {
        //Should be run async!
        //Create item
        item = new ItemStack(Material.ENCHANTED_BOOK, 1);
        ItemMeta meta = item.getItemMeta();

        //Set display
        assert meta!= null;
        meta.setDisplayName(displayName);

        //Set button data
        NamespacedKey typeKey = new NamespacedKey(TraincartsGui.getPlugin(), "type");
        NamespacedKey buttonKey = new NamespacedKey(TraincartsGui.getPlugin(), "button_type");
        NamespacedKey guiKey = new NamespacedKey(TraincartsGui.getPlugin(), "gui");

        meta.getPersistentDataContainer().set(typeKey, PersistentDataType.STRING, "button");
        meta.getPersistentDataContainer().set(buttonKey, PersistentDataType.STRING, "linker");
        meta.getPersistentDataContainer().set(guiKey, PersistentDataType.INTEGER, linkedGuiId);

        item.setItemMeta(meta);
    }
}
