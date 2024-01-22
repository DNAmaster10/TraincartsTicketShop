package com.dnamaster10.tcgui.util.eventhandlers;

import com.dnamaster10.tcgui.util.database.PlayerAccessor;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.SQLException;

import static com.dnamaster10.tcgui.TraincartsGui.getPlugin;

public class PlayerJoinEventHandler implements Listener {
    @EventHandler
    void onPlayerJoin(PlayerJoinEvent event) {
        //Update player in database
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            try {
                PlayerAccessor accessor = new PlayerAccessor();
                accessor.updatePlayer(event.getPlayer().getDisplayName(), event.getPlayer().getUniqueId().toString());
            } catch (SQLException e) {
                getPlugin().reportSqlError(e.toString());
            }
        });
    }
}
