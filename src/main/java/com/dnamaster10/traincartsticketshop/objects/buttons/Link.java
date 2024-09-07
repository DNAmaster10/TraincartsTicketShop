package com.dnamaster10.traincartsticketshop.objects.buttons;

import com.dnamaster10.traincartsticketshop.util.database.databaseobjects.LinkDatabaseObject;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import static com.dnamaster10.traincartsticketshop.objects.buttons.DataKeys.*;

public class Link extends Button {
    private final String displayName;
    private final int linkedGuiId;
    private final int linkedGuiPage;
    public LinkDatabaseObject getAsDatabaseObject(int slot) {
        String rawDisplayName = ChatColor.stripColor(displayName);
        return new LinkDatabaseObject(slot, linkedGuiId, linkedGuiPage, displayName, rawDisplayName);
    }
    public ItemStack getItemStack() {
        ItemStack item = new ItemStack(Material.ENCHANTED_BOOK, 1);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName(this.displayName);

        PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
        dataContainer.set(BUTTON_TYPE, PersistentDataType.STRING, "link");
        dataContainer.set(DEST_GUI_ID, PersistentDataType.INTEGER, linkedGuiId);
        dataContainer.set(DEST_GUI_PAGE, PersistentDataType.INTEGER, linkedGuiPage);

        item.setItemMeta(meta);
        return item;
    }
    public Link(int linkedGuiId, int linkedGuiPage, String displayName) {
        this.displayName = displayName;
        this.linkedGuiId = linkedGuiId;
        this.linkedGuiPage = linkedGuiPage;
    }

    public Link(LinkDatabaseObject link) {
        displayName = link.colouredDisplayName();
        linkedGuiId = link.linkedGuiId();
        linkedGuiPage = link.linkedGuiPage();
    }
}
