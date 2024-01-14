package com.dnamaster10.tcgui.util.eventhandlers;

import com.dnamaster10.tcgui.TraincartsGui;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitEventHandler implements Listener {
    @EventHandler
    void onPlayerQuit(PlayerQuitEvent event) {
        TraincartsGui.getPlugin().getGuiManager().handleLeaveEvent(event.getPlayer());
    }
}
