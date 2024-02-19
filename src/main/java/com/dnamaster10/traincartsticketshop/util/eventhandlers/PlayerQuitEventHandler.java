package com.dnamaster10.traincartsticketshop.util.eventhandlers;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import static com.dnamaster10.traincartsticketshop.TraincartsTicketShop.getPlugin;

public class PlayerQuitEventHandler implements Listener {
    @EventHandler
    void onPlayerQuit(PlayerQuitEvent event) {
        getPlugin().getGuiManager().handlePlayerLeave(event.getPlayer());
    }
}
