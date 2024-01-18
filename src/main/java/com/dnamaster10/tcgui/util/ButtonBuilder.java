package com.dnamaster10.tcgui.util;

import com.dnamaster10.tcgui.TraincartsGui;
import com.dnamaster10.tcgui.util.database.GuiAccessor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.sql.SQLException;

public class ButtonBuilder {
    //For creating UI buttons
    public ItemStack getNextPageButton() {
        //Create the item
        ItemStack nextPageItem = new ItemStack(Material.BOOK, 1);
        ItemMeta meta = nextPageItem.getItemMeta();
        assert meta != null;

        //Set display text
        meta.setDisplayName("Next Page");

        //Set button type
        NamespacedKey typeKey = new NamespacedKey(TraincartsGui.getPlugin(), "type");
        NamespacedKey buttonKey = new NamespacedKey(TraincartsGui.getPlugin(), "button_type");

        meta.getPersistentDataContainer().set(typeKey, PersistentDataType.STRING, "button");
        meta.getPersistentDataContainer().set(buttonKey, PersistentDataType.STRING, "next_page");
        nextPageItem.setItemMeta(meta);

        return nextPageItem;
    }
    public ItemStack getPrevPageButton() {
        //Create the item
        ItemStack prevPageItem = new ItemStack(Material.BOOK, 1);
        ItemMeta meta = prevPageItem.getItemMeta();
        assert  meta != null;

        //Set display
        meta.setDisplayName("Prev Page");

        //Set button type
        NamespacedKey typeKey = new NamespacedKey(TraincartsGui.getPlugin(), "type");
        NamespacedKey buttonKey = new NamespacedKey(TraincartsGui.getPlugin(), "button_type");

        meta.getPersistentDataContainer().set(typeKey, PersistentDataType.STRING, "button");
        meta.getPersistentDataContainer().set(buttonKey, PersistentDataType.STRING, "prev_page");
        prevPageItem.setItemMeta(meta);

        return prevPageItem;
    }
    public ItemStack getBackButton() {
        //Create the item
        ItemStack backPageItem = new ItemStack(Material.COMPASS, 1);
        ItemMeta meta = backPageItem.getItemMeta();

        //Set display
        assert meta != null;
        meta.setDisplayName("Back");

        //Set button data
        NamespacedKey typeKey = new NamespacedKey(TraincartsGui.getPlugin(), "type");
        NamespacedKey buttonKey = new NamespacedKey(TraincartsGui.getPlugin(), "button_type");

        meta.getPersistentDataContainer().set(typeKey, PersistentDataType.STRING, "button");
        meta.getPersistentDataContainer().set(buttonKey, PersistentDataType.STRING, "back");
        backPageItem.setItemMeta(meta);

        return backPageItem;
    }
    public ItemStack getLinkerButton(String linkedGuiName) throws SQLException {
        //Should be run async!
        //Create the item
        ItemStack linkerButton = new ItemStack(Material.ENCHANTED_BOOK, 1);
        ItemMeta meta = linkerButton.getItemMeta();

        //Set display
        GuiAccessor guiAccessor = new GuiAccessor();
        String displayName = guiAccessor.getGuiDisplayName(linkedGuiName);
        assert meta != null;
        meta.setDisplayName(displayName);

        //Set button data
        NamespacedKey typeKey = new NamespacedKey(TraincartsGui.getPlugin(), "type");
        NamespacedKey buttonKey = new NamespacedKey(TraincartsGui.getPlugin(), "button_type");
        NamespacedKey guiKey = new NamespacedKey(TraincartsGui.getPlugin(), "gui");

        meta.getPersistentDataContainer().set(typeKey, PersistentDataType.STRING, "button");
        meta.getPersistentDataContainer().set(buttonKey, PersistentDataType.STRING, "linker");
        meta.getPersistentDataContainer().set(guiKey, PersistentDataType.STRING, linkedGuiName);

        linkerButton.setItemMeta(meta);

        return linkerButton;
    }
}










