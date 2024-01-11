package com.dnamaster10.tcgui.util;

import com.dnamaster10.tcgui.objects.Ticket;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Tickets {
    public static void createTicketCommand(Player p, String tcName, String displayName) {
        //First check that the ticket exists in traincarts
        if (!Traincarts.checkTicket(tcName)) {
            p.sendMessage(ChatColor.RED + "No traincarts ticket with name \"" + tcName + "\" exists");
            return;
        }
        Ticket ticket = new Ticket(tcName, displayName);
        ticket.giveToPlayer(p);
    }
    public static void editTicketDisplayCommand(Player p, String displayName) {

    }
}
