package com.dnamaster10.traincartsticketshop.util;

import com.bergerkiller.bukkit.tc.tickets.Ticket;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static com.bergerkiller.bukkit.tc.tickets.TicketStore.getTicket;

public class Traincarts {
    //Contains methods which interact or involve the traincarts plugin
    public static boolean checkTicket(String name) {
        //Returns true if traincarts ticket exists
        return getTicket(name) != null;
    }
    public static void giveTicketItem(String tcName, Player p) {
        Ticket ticket = getTicket(tcName);

        ItemStack item = ticket.createItem(p);
        p.getInventory().addItem(item);
    }
}
