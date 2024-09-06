package com.dnamaster10.traincartsticketshop.objects.guis2;

import com.dnamaster10.traincartsticketshop.objects.buttons.Button;
import com.dnamaster10.traincartsticketshop.objects.buttons.SimpleHeadButton;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import static com.dnamaster10.traincartsticketshop.objects.buttons.HeadData.HeadType.*;

public class Page {
    //Holds the contents of an inventory
    private final Button[] page = new Button[54];
    private String displayName = "Inventory";

    public Button[] getPage() {
        return page;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    //TODO possibly use inventory.setStorageContents(ItemStack[] items);
    public Inventory getAsInventory(InventoryHolder inventoryHolder) {
        Inventory inventory = Bukkit.createInventory(inventoryHolder, 54, displayName);
        for (int slot = 0; slot < 54; slot++) {
            if (page[slot] != null) {
                inventory.setItem(slot, page[slot].getItemStack());
            }
        }
        return inventory;
    }

    public void addButton(int slot, Button button) {
        page[slot] = button;
    }

    public void addBackButton() {
        SimpleHeadButton button = new SimpleHeadButton("back", GRAY_BACK_ARROW, "Back");
        page[45] = button;
    }

    public void addNextPageButton() {
        SimpleHeadButton button = new SimpleHeadButton("next_page", CHAT_ARROW_RIGHT, "Next Page");
        page[53] = button;
    }

    public void addPrevPageButton() {
        SimpleHeadButton button = new SimpleHeadButton("prev_page", CHAT_ARROW_LEFT, "Prev Page");
        page[52] = button;
    }
}
