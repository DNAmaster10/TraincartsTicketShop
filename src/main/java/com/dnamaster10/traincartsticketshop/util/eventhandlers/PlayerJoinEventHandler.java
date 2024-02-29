package com.dnamaster10.traincartsticketshop.util.eventhandlers;

import com.dnamaster10.traincartsticketshop.util.database.mariadb.MariaDBPlayerAccessor;
import com.dnamaster10.traincartsticketshop.util.exceptions.ModificationException;
import com.dnamaster10.traincartsticketshop.util.exceptions.QueryException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import static com.dnamaster10.traincartsticketshop.TraincartsTicketShop.getPlugin;

public class PlayerJoinEventHandler implements Listener {
    @EventHandler
    void onPlayerJoin(PlayerJoinEvent event) {
        //Update player in database
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            try {
                MariaDBPlayerAccessor accessor = new MariaDBPlayerAccessor();
                Player p = event.getPlayer();
                accessor.updatePlayer(p.getDisplayName(), p.getUniqueId().toString());
            } catch (QueryException | ModificationException e) {
                getPlugin().handleSqlException(e);
            }
        });
    }
}
