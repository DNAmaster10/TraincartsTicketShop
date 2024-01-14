package com.dnamaster10.tcgui.util;

import com.dnamaster10.tcgui.objects.Ticket;
import com.dnamaster10.tcgui.util.database.GuiAccessor;
import com.dnamaster10.tcgui.util.database.TicketAccessor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;

public class GuiBuilder {
    //For fetching gui info from the database and building it into an Inventory object
    private String guiName;
    private int pageNumber;
    //Used to decide whether a new page button should be created
    public Inventory build() throws SQLException {
        //Fetches tickets and builds an inventory with them.
        //Only does the top rows excluding the bottom row since the bottom row contains UI elements
        //Must be executed asynchronously or server will freeze with database calls
        Inventory inventory = Bukkit.createInventory(null, 54);

        //Page buttons
        inventory.setItem (53, new ItemStack(Material.BOOK, 1));

        //Add tickets
        //Get gui ID
        GuiAccessor guiAccessor = new GuiAccessor();
        int guiId = guiAccessor.getGuiIdByName(this.guiName);
        TicketAccessor ticketAccessor = new TicketAccessor();
        TicketDatabaseObject[] ticketDatabaseObjects = ticketAccessor.getTickets(guiId, pageNumber);

        //Add tickets to inventory
        for (TicketDatabaseObject dbObject : ticketDatabaseObjects) {
            Ticket ticket = new Ticket(dbObject.getTcName(), dbObject.getDisplayName(), dbObject.getPrice());
            ItemStack stack = new ItemStack(Material.PAPER, 1);
            inventory.setItem(dbObject.getSlot(), stack);
        }
        return inventory;
    }
    public GuiBuilder(String guiName, int pageNumber) {
        this.guiName = guiName;
        this.pageNumber = pageNumber;
    }
}
