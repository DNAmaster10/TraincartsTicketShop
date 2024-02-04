package com.dnamaster10.tcgui.objects.guis;

import com.dnamaster10.tcgui.objects.buttons.*;
import com.dnamaster10.tcgui.util.database.GuiAccessor;
import com.dnamaster10.tcgui.util.database.LinkerAccessor;
import com.dnamaster10.tcgui.util.database.TicketAccessor;
import com.dnamaster10.tcgui.util.database.databaseobjects.LinkerDatabaseObject;
import com.dnamaster10.tcgui.util.database.databaseobjects.TicketDatabaseObject;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;

public class GuiBuilder {
    //For fetching gui info from the database and building it into an Inventory object
    private final Inventory inventory;
    public Inventory getInventory() {
        return this.inventory;
    }
    public void addItem(int slot, ItemStack item) {
        inventory.setItem(slot, item);
    }
    public void addTickets(TicketDatabaseObject[] tickets) {
        for (TicketDatabaseObject dbObject : tickets) {
            Ticket ticket = new Ticket(dbObject.getTcName(), dbObject.getColouredDisplayName(), dbObject.getPrice());
            inventory.setItem(dbObject.getSlot(), ticket.getItemStack());
        }
    }
    public void addTicketsFromDatabase(String guiName, int pageNumber) throws SQLException {
        //Fetches tickets and builds an inventory with them.
        //Only does the top rows excluding the bottom row since the bottom row contains UI elements
        //Must be executed asynchronously or server will freeze with database calls

        //Add tickets
        //Get gui ID
        GuiAccessor guiAccessor = new GuiAccessor();
        int guiId = guiAccessor.getGuiIdByName(guiName);
        TicketAccessor ticketAccessor = new TicketAccessor();
        TicketDatabaseObject[] ticketDatabaseObjects = ticketAccessor.getTickets(guiId, pageNumber);

        //Add tickets to inventory
        addTickets(ticketDatabaseObjects);
    }
    public void addLinkers(LinkerDatabaseObject[] linkers) {
        for (LinkerDatabaseObject linker : linkers) {
            Linker linkerButton = new Linker(linker.getLinkedGuiId(), linker.getLinkedGuiPage(), linker.getColouredDisplayName());
            inventory.setItem(linker.getSlot(), linkerButton.getItemStack());
        }
    }
    public void addLinkersFromDatabase(String guiName, int pageNumber) throws SQLException {
        //Must be called from async thread
        //Get gui ID
        GuiAccessor guiAccessor = new GuiAccessor();
        int guiId = guiAccessor.getGuiIdByName(guiName);

        //Get the linkers
        LinkerAccessor linkerAccessor = new LinkerAccessor();
        LinkerDatabaseObject[] linkers = linkerAccessor.getLinkersByGuiId(guiId, pageNumber);

        //Add linkers to inventory
        addLinkers(linkers);
    }
    public void addSimpleButton(SimpleButton button, int slot) {
        this.inventory.setItem(slot, button.getItemStack());
    }
    public void addNextPageButton() {
        SimpleButton button = new SimpleButton("next_page", HeadData.HeadType.CHAT_ARROW_RIGHT, "Next Page");
        this.inventory.setItem(53, button.getItemStack());
    }
    public void addPrevPageButton() {
        SimpleButton button = new SimpleButton("prev_page", HeadData.HeadType.CHAT_ARROW_LEFT, "Prev Page");
        this.inventory.setItem(52, button.getItemStack());
    }
    public void addBackButton() {
        SimpleButton button = new SimpleButton("back", HeadData.HeadType.GRAY_BACK_ARROW, "back");
        this.inventory.setItem(45, button.getItemStack());
    }

    public GuiBuilder(String displayName) {
        this.inventory = Bukkit.createInventory(null, 54, displayName);
    }
}
