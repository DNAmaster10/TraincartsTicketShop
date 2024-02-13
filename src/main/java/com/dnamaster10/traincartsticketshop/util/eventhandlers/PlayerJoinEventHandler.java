package com.dnamaster10.traincartsticketshop.util.eventhandlers;

import com.dnamaster10.traincartsticketshop.util.database.PlayerAccessor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.SQLException;

import static com.dnamaster10.traincartsticketshop.TraincartsTicketShop.getPlugin;

public class PlayerJoinEventHandler implements Listener {
    @EventHandler
    void onPlayerJoin(PlayerJoinEvent event) {
        //Update player in database
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            try {
                PlayerAccessor accessor = new PlayerAccessor();
                Player p = event.getPlayer();
                accessor.updatePlayer(p.getDisplayName(), p.getUniqueId().toString());
            } catch (SQLException e) {
                getPlugin().reportSqlError(e);
            }
        });
    }
}