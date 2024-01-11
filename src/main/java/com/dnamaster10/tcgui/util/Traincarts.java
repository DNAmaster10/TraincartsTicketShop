package com.dnamaster10.tcgui.util;

import static com.bergerkiller.bukkit.tc.tickets.TicketStore.getTicket;

public class Traincarts {
    //Contains methods which interact or involve the traincarts plugin
    public static boolean checkTicket(String name) {
        //Returns true if traincarts ticket exists
        return getTicket(name) != null;
    }
}
