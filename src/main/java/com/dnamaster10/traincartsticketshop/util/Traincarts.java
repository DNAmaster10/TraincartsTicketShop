package com.dnamaster10.traincartsticketshop.util;

import com.bergerkiller.bukkit.tc.tickets.Ticket;
import com.bergerkiller.bukkit.tc.tickets.TicketStore;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.bergerkiller.bukkit.tc.tickets.TicketStore.*;

public class Traincarts {
    //Contains methods which interact or involve the traincarts plugin
    public static boolean checkTicket(String name) {
        //Returns true if traincarts ticket exists
        return getTicket(name) != null;
    }
    public static boolean isTraincartsTicket(ItemStack item) {
        return isTicketItem(item);
    }
    public static com.dnamaster10.traincartsticketshop.objects.buttons.Ticket getAsTicketShopTicket(ItemStack traincartsTicketItem) {
        Ticket traincartsTicket = getTicketFromItem(traincartsTicketItem);
        if (traincartsTicket == null) return null;
        String ticketName = traincartsTicket.getName();
        return new com.dnamaster10.traincartsticketshop.objects.buttons.Ticket(ticketName, ticketName, null);
    }
    public static void giveTicketItem(String tcName, Player p) {
        Ticket ticket = getTicket(tcName);

        ItemStack item = ticket.createItem(p);
        p.getInventory().addItem(item);
    }
    private static List<String> getTicketNames() {
        return TicketStore.getAll().stream()
                .map(Ticket::getName)
                .collect(Collectors.toList());
    }
    public static List<String> getPartialTicketNameCompletions(String inputString) {
        return StringUtil.copyPartialMatches(inputString, getTicketNames(), new ArrayList<>());
    }
}
