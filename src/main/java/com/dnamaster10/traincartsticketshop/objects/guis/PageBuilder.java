package com.dnamaster10.traincartsticketshop.objects.guis;

import com.dnamaster10.traincartsticketshop.objects.buttons.*;
import com.dnamaster10.traincartsticketshop.util.ButtonUtils;
import com.dnamaster10.traincartsticketshop.util.database.LinkerAccessor;
import com.dnamaster10.traincartsticketshop.util.database.TicketAccessor;
import com.dnamaster10.traincartsticketshop.util.database.databaseobjects.LinkerDatabaseObject;
import com.dnamaster10.traincartsticketshop.util.database.databaseobjects.TicketDatabaseObject;
import com.dnamaster10.traincartsticketshop.util.exceptions.DQLException;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import static com.dnamaster10.traincartsticketshop.util.ButtonUtils.getButtonType;
import static com.dnamaster10.traincartsticketshop.objects.buttons.HeadData.HeadType.*;

public class PageBuilder {
    private final Button[] page = new Button[54];
    public Button[] getPage() {
        return this.page;
    }
    private boolean checkButtonDisplayName(ItemStack item) {
        //Returns false if display name of item is too long or too short. Should be used for tickets and linkers.
        if (!item.hasItemMeta()) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        String colouredDisplayName = meta.getDisplayName();

        if (colouredDisplayName.length() > 100) {
            return false;
        }
        String rawDisplayName = ChatColor.stripColor(colouredDisplayName);

        return rawDisplayName.length() <= 25 && !rawDisplayName.isBlank();
    }
    public void addInventory(Inventory inventory) {
        if (inventory == null) {
            return;
        }
        //Builds the page from an inventory
        ItemStack[] contents = inventory.getContents();
        for (int i = 0; i < contents.length; i++) {
            ItemStack item = contents[i];

            //Check if the item is a Traincartsticketshop button
            if (item == null) {
                continue;
            }
            String buttonType = getButtonType(item);
            if (buttonType == null) {
                continue;
            }

            if (!checkButtonDisplayName(item)) {
                continue;
            }

            //Item is button, create a new button object from the item
            Button button = ButtonUtils.getNewButton(buttonType, item);
            page[i] = button;
        }
    }
    public void addTickets(TicketDatabaseObject[] tickets) {
        for (TicketDatabaseObject ticketDatabaseObject : tickets) {
            if (ticketDatabaseObject == null) {
                continue;
            }
            //Create the button
            Ticket ticket = new Ticket(ticketDatabaseObject.tcName(), ticketDatabaseObject.colouredDisplayName(), ticketDatabaseObject.purchaseMessage());
            page[ticketDatabaseObject.slot()] = ticket;
        }
    }
    public void addLinkers(LinkerDatabaseObject[] linkers) {
        for (LinkerDatabaseObject linkerDatabaseObject : linkers) {
            if (linkerDatabaseObject == null) {
                continue;
            }
            //Create the ticket
            Linker linker = new Linker(linkerDatabaseObject.linkedGuiId(), linkerDatabaseObject.linkedGuiPage(), linkerDatabaseObject.colouredDisplayName());
            page[linkerDatabaseObject.slot()] = linker;
        }
    }
    public void addTicketsFromDatabase(int guiId, int pageNumber) throws DQLException {
        //Fetches tickets from database and adds their buttons to the gui
        TicketAccessor ticketAccessor = new TicketAccessor();
        TicketDatabaseObject[] ticketDatabaseObjects = ticketAccessor.getTickets(guiId, pageNumber);
        addTickets(ticketDatabaseObjects);
    }
    public void addLinkersFromDatabase(int guiId, int pageNumber) throws DQLException {
        //Fetches linkers from the database and adds their buttons to the gui
        LinkerAccessor linkerAccessor = new LinkerAccessor();
        LinkerDatabaseObject[] linkerDatabaseObjects = linkerAccessor.getLinkersByGuiId(guiId, pageNumber);
        addLinkers(linkerDatabaseObjects);
    }
    public void addButton(int slot, Button button) {
        page[slot] = button;
    }
    public void addNextPageButton() {
        SimpleHeadButton button = new SimpleHeadButton("next_page", CHAT_ARROW_RIGHT, "Next Page");
        addButton(53, button);
    }
    public void addPrevPageButton() {
        SimpleHeadButton button = new SimpleHeadButton("prev_page", CHAT_ARROW_LEFT, "Prev Page");
        addButton(52, button);
    }
    public void addBackButton() {
        SimpleHeadButton button = new SimpleHeadButton("back", GRAY_BACK_ARROW, "Back");
        addButton(45, button);
    }
}
