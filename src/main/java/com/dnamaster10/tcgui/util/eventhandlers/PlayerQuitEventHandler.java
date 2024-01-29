package com.dnamaster10.tcgui.util.eventhandlers;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import static com.dnamaster10.tcgui.TraincartsGui.getPlugin;

public class PlayerQuitEventHandler implements Listener {
    @EventHandler
    void onPlayerQuit(PlayerQuitEvent event) {
        getPlugin().getGuiManager().handleLeaveEvent(event.getPlayer());
    }
}
