package com.dnamaster10.tcgui.util.gui;

import com.dnamaster10.tcgui.objects.buttons.*;
import com.dnamaster10.tcgui.util.database.GuiAccessor;
import com.dnamaster10.tcgui.util.database.LinkerAccessor;
import com.dnamaster10.tcgui.util.database.TicketAccessor;
import com.dnamaster10.tcgui.util.database.databaseobjects.LinkerDatabaseObject;
import com.dnamaster10.tcgui.util.database.databaseobjects.TicketDatabaseObject;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import java.sql.SQLException;

public class GuiBuilder {
    //For fetching gui info from the database and building it into an Inventory object
    private String guiName;
    private int pageNumber;
    private Inventory inventory;
    //Used to decide whether a new page button should be created
    public void addTickets() throws SQLException {
        //Fetches tickets and builds an inventory with them.
        //Only does the top rows excluding the bottom row since the bottom row contains UI elements
        //Must be executed asynchronously or server will freeze with database calls

        //Add tickets
        //Get gui ID
        GuiAccessor guiAccessor = new GuiAccessor();
        int guiId = guiAccessor.getGuiIdByName(this.guiName);
        TicketAccessor ticketAccessor = new TicketAccessor();
        TicketDatabaseObject[] ticketDatabaseObjects = ticketAccessor.getTickets(guiId, pageNumber);

        //Add tickets to inventory
        for (TicketDatabaseObject dbObject : ticketDatabaseObjects) {
            Ticket ticket = new Ticket(dbObject.getTcName(), dbObject.getDisplayName(), dbObject.getPrice());
            inventory.setItem(dbObject.getSlot(), ticket.getItemStack());
        }
    }
    public void addLinkers() throws SQLException {
        //Must be called from async thread
        //Get gui ID
        GuiAccessor guiAccessor = new GuiAccessor();
        int guiId = guiAccessor.getGuiIdByName(this.guiName);

        //Get the linkers
        LinkerAccessor linkerAccessor = new LinkerAccessor();
        LinkerDatabaseObject[] linkers = linkerAccessor.getLinkersByGuiId(guiId, pageNumber);

        //Add linkers to inventory
        for (LinkerDatabaseObject dbObject : linkers) {
            LinkerButton linker = new LinkerButton(dbObject.getLinkedGuiId(), dbObject.getDisplayName());
            inventory.setItem(dbObject.getSlot(), linker.getItemStack());
        }
    }
    public void addNextPageButton() {
        NextPageButton button = new NextPageButton();
        this.inventory.setItem(53, button.getItemStack());
    }
    public void addPrevPageButton() {
        PrevPageButton button = new PrevPageButton();
        this.inventory.setItem(52, button.getItemStack());
    }
    public void addBackButton() {
        BackButton button = new BackButton();
        this.inventory.setItem(45, button.getItemStack());
    }
    public Inventory getInventory() {
        return this.inventory;
    }
    public GuiBuilder(String guiName, int pageNumber) {
        this.inventory = Bukkit.createInventory(null, 54);
        this.guiName = guiName;
        this.pageNumber = pageNumber;
    }
}
