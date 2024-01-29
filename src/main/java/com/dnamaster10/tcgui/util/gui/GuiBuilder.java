package com.dnamaster10.tcgui.util.gui;

import com.dnamaster10.tcgui.objects.buttons.*;
import com.dnamaster10.tcgui.util.database.GuiAccessor;
import com.dnamaster10.tcgui.util.database.LinkerAccessor;
import com.dnamaster10.tcgui.util.database.TicketAccessor;
import com.dnamaster10.tcgui.util.database.databaseobjects.LinkerDatabaseObject;
import com.dnamaster10.tcgui.util.database.databaseobjects.TicketDatabaseObject;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;
import java.sql.SQLException;

public class GuiBuilder {
    //For fetching gui info from the database and building it into an Inventory object
    private final String guiName;
    private final int pageNumber;
    private final Inventory inventory;
    //Used to decide whether a new page button should be created
    private void addTicketsToInventory(TicketDatabaseObject[] ticketList) {
        for (TicketDatabaseObject dbObject : ticketList) {
            Ticket ticket = new Ticket(dbObject.getTcName(), dbObject.getColouredDisplayName(), dbObject.getPrice());
            inventory.setItem(dbObject.getSlot(), ticket.getItemStack());
        }
    }
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
        addTicketsToInventory(ticketDatabaseObjects);
    }
    public void addTickets(TicketDatabaseObject[] ticketList) {
        addTicketsToInventory(ticketList);
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
            LinkerButton linker = new LinkerButton(dbObject.getLinkedGuiId(), dbObject.getColouredDisplayName());
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
    public void addSearchButton() {
        SearchButton searchButton = new SearchButton();
        this.inventory.setItem(49, searchButton.getItemStack());
    }
    public Inventory getInventory() {
        return this.inventory;
    }
    public GuiBuilder(String guiName, int pageNumber) throws SQLException {
        //Should be called async
        GuiAccessor guiAccessor = new GuiAccessor();

        String guiDisplayName = guiAccessor.getColouredGuiDisplayName(guiName);
        this.inventory = Bukkit.createInventory(null, 54, ChatColor.translateAlternateColorCodes('&', guiDisplayName));
        this.guiName = guiName;
        this.pageNumber = pageNumber;
    }
    public GuiBuilder(String guiName) throws SQLException {
        //Used when we don't need to fetch tickets from the database within this method (Such as with the search gui)
       this(guiName, 0);
    }
}
