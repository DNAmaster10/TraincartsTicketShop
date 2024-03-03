package com.dnamaster10.traincartsticketshop.objects.guis;

import com.dnamaster10.traincartsticketshop.objects.buttons.*;
import com.dnamaster10.traincartsticketshop.util.ButtonUtils;
import com.dnamaster10.traincartsticketshop.util.Traincarts;
import com.dnamaster10.traincartsticketshop.util.database.AccessorFactory;
import com.dnamaster10.traincartsticketshop.util.database.accessorinterfaces.LinkAccessor;
import com.dnamaster10.traincartsticketshop.util.database.accessorinterfaces.TicketAccessor;
import com.dnamaster10.traincartsticketshop.util.database.databaseobjects.LinkDatabaseObject;
import com.dnamaster10.traincartsticketshop.util.database.databaseobjects.TicketDatabaseObject;
import com.dnamaster10.traincartsticketshop.util.exceptions.QueryException;
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
        //Returns false if display name of item is too long or too short. Should be used for tickets and links.
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
            if (Traincarts.isTraincartsTicket(item)) {
                //Convert the ticket to a ticket shop item
                Ticket ticket = Traincarts.getAsTicketShopTicket(item);
                page[i] = ticket;
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
    public void addLinks(LinkDatabaseObject[] links) {
        for (LinkDatabaseObject linkDatabaseObject : links) {
            if (linkDatabaseObject == null) {
                continue;
            }
            //Create the ticket
            Link link = new Link(linkDatabaseObject.linkedGuiId(), linkDatabaseObject.linkedGuiPage(), linkDatabaseObject.colouredDisplayName());
            page[linkDatabaseObject.slot()] = link;
        }
    }
    public void addTicketsFromDatabase(int guiId, int pageNumber) throws QueryException {
        //Fetches tickets from database and adds their buttons to the gui
        TicketAccessor ticketAccessor = AccessorFactory.getTicketAccessor();
        TicketDatabaseObject[] ticketDatabaseObjects = ticketAccessor.getTickets(guiId, pageNumber);
        addTickets(ticketDatabaseObjects);
    }
    public void addLinksFromDatabase(int guiId, int pageNumber) throws QueryException {
        //Fetches links from the database and adds their buttons to the gui
        LinkAccessor linkAccessor = AccessorFactory.getLinkAccessor();
        LinkDatabaseObject[] linkDatabaseObjects = linkAccessor.getLinksByGuiId(guiId, pageNumber);
        addLinks(linkDatabaseObjects);
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
