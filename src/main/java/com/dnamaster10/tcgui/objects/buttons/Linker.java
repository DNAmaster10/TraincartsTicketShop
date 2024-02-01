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

public class Linker extends Button {
    public void giveToPlayer(Player p) {
        p.getInventory().addItem(item);
    }
    public Linker(int linkedGuiId, int linkedGuiPage, String displayName) {
        //Create item
        item = new ItemStack(Material.ENCHANTED_BOOK, 1);
        ItemMeta meta = item.getItemMeta();

        //Set display
        assert meta != null;
        meta.setDisplayName(displayName);

        //Set button data
        PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
        dataContainer.set(BUTTON_TYPE, PersistentDataType.STRING, "linker");
        dataContainer.set(DEST_GUI_ID, PersistentDataType.INTEGER, linkedGuiId);
        dataContainer.set(DEST_GUI_PAGE, PersistentDataType.INTEGER, linkedGuiPage);

        item.setItemMeta(meta);
    }
    public Linker(int linkedGuiId, String displayName) {
        this(linkedGuiId, 0, displayName);
    }
}
