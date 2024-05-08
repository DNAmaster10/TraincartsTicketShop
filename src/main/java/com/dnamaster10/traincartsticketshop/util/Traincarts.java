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

    /**
     * Returns a boolean indicating whether a given traincarts ticket exists
     *
     * @param name the traincarts ticket name
     * @return true if the ticket exists
     * */
    public static boolean checkTicket(String name) {
        return getTicket(name) != null;
    }

    /**
     * Returns a boolean indicating whether a given ItemStack is a traincarts ticket
     *
     * @param item an ItemStack
     * @return true if the ItemStack is a traincarts ticket
     * */
    public static boolean isTraincartsTicket(ItemStack item) {
        return isTicketItem(item);
    }

    /**
     * Takes in a Traincarts ticket and returns a Ticket Shop ticket
     *
     * @param traincartsTicketItem a traincarts ticket ItemStack
     * @return a Ticket Shop Ticket ItemStack
     * */
    public static com.dnamaster10.traincartsticketshop.objects.buttons.Ticket getAsTicketShopTicket(ItemStack traincartsTicketItem) {
        Ticket traincartsTicket = getTicketFromItem(traincartsTicketItem);
        if (traincartsTicket == null) return null;
        String ticketName = traincartsTicket.getName();
        return new com.dnamaster10.traincartsticketshop.objects.buttons.Ticket(ticketName, ticketName, null);
    }

    /**
     * Takes in a Traincarts ticket name and a player and gives the player a Traincarts ticket
     *
     * @param tcName a traincarts ticket name
     * @param p the player to give the ticket to
     * */
    public static void giveTicketItem(String tcName, Player p) {
        Ticket ticket = getTicket(tcName);

        ItemStack item = ticket.createItem(p);
        p.getInventory().addItem(item);
    }

    /**
     * Returns a list of all ticket names available in Traincarts
     *
     * @return a list of traincarts ticket names
     * */
    private static List<String> getTicketNames() {
        return TicketStore.getAll().stream()
                .map(Ticket::getName)
                .collect(Collectors.toList());
    }

    /**
     * Returns a list of traincarts ticket names which at least partially match the input string
     *
     * @param inputString the string to compare the ticket names to
     * @return a list of traincarts ticket names which partially match the input string
     * */
    public static List<String> getPartialTicketNameCompletions(String inputString) {
        return StringUtil.copyPartialMatches(inputString, getTicketNames(), new ArrayList<>());
    }
}
