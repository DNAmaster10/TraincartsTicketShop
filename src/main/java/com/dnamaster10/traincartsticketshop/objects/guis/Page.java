package com.dnamaster10.traincartsticketshop.objects.guis;

import com.dnamaster10.traincartsticketshop.objects.buttons.Button;
import com.dnamaster10.traincartsticketshop.objects.buttons.Link;
import com.dnamaster10.traincartsticketshop.objects.buttons.SimpleHeadButton;
import com.dnamaster10.traincartsticketshop.objects.buttons.Ticket;
import com.dnamaster10.traincartsticketshop.util.ButtonUtils;
import com.dnamaster10.traincartsticketshop.util.Traincarts;
import com.dnamaster10.traincartsticketshop.util.database.databaseobjects.LinkDatabaseObject;
import com.dnamaster10.traincartsticketshop.util.database.databaseobjects.TicketDatabaseObject;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import static com.dnamaster10.traincartsticketshop.objects.buttons.HeadData.HeadType.*;
import static com.dnamaster10.traincartsticketshop.util.ButtonUtils.getButtonType;

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

    public void addFromTicketDatabaseObjects(TicketDatabaseObject[] tickets) {
        for (TicketDatabaseObject ticketDatabaseObject : tickets) {
            if (ticketDatabaseObject == null) continue;
            Ticket ticket = new Ticket(ticketDatabaseObject);
            page[ticketDatabaseObject.slot()] = ticket;
        }
    }

    public void addFromLinkDatabaseObjects(LinkDatabaseObject[] links) {
        for (LinkDatabaseObject linkDatabaseObject : links) {
            if (linkDatabaseObject == null) continue;
            Link link = new Link(linkDatabaseObject);
            page[linkDatabaseObject.slot()] = link;
        }
    }

    public void addInventory(Inventory inventory) {
        if (inventory == null) return;
        ItemStack[] contents = inventory.getContents();
        for (int i = 0; i < contents.length; i++) {
            ItemStack item = contents[i];

            if (item == null) continue;
            if (Traincarts.isTraincartsTicket(item)) {
                Ticket ticket = Traincarts.getAsTicketShopTicket(item);
                if (ticket == null) continue;
                page[i] = ticket;
                continue;
            }

            String buttonType = getButtonType(item);
            if (buttonType == null) continue;
            if (!item.hasItemMeta()) continue;

            ItemMeta meta = item.getItemMeta();
            assert meta != null;
            String colouredDisplayName = meta.getDisplayName();
            if (colouredDisplayName.length() > 100) continue;
            String rawDisplayName = ChatColor.stripColor(colouredDisplayName);
            if (rawDisplayName.length() <= 25 && !rawDisplayName.isBlank()) continue;

            //Item is a button, create a new button object from the button
            Button button = ButtonUtils.getNewButton(buttonType, item);
            page[i] = button;
        }
    }












































}
