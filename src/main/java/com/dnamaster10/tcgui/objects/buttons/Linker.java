package com.dnamaster10.tcgui.objects.buttons;

import com.dnamaster10.tcgui.util.database.databaseobjects.LinkerDatabaseObject;
import org.bukkit.ChatColor;
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
    private final String displayName;
    private final int linkedGuiId;
    private final int linkedGuiPage;
    public LinkerDatabaseObject getAsDatabaseObject(int slot) {
        String rawDisplayName = ChatColor.stripColor(displayName);
        return new LinkerDatabaseObject(slot, linkedGuiId, linkedGuiPage, displayName, rawDisplayName);
    }
    public ItemStack getItemStack() {
        ItemStack item = new ItemStack(Material.ENCHANTED_BOOK, 1);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName(this.displayName);

        PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
        dataContainer.set(BUTTON_TYPE, PersistentDataType.STRING, "linker");
        dataContainer.set(DEST_GUI_ID, PersistentDataType.INTEGER, linkedGuiId);
        dataContainer.set(DEST_GUI_PAGE, PersistentDataType.INTEGER, linkedGuiPage);

        item.setItemMeta(meta);
        return item;
    }
    public Linker(int linkedGuiId, int linkedGuiPage, String displayName) {
        this.displayName = displayName;
        this.linkedGuiId = linkedGuiId;
        this.linkedGuiPage = linkedGuiPage;
    }
}
