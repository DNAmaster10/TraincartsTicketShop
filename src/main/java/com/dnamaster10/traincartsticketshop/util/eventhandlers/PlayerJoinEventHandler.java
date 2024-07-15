package com.dnamaster10.traincartsticketshop.util.eventhandlers;

import com.dnamaster10.traincartsticketshop.TraincartsTicketShop;
import com.dnamaster10.traincartsticketshop.util.exceptions.ModificationException;
import com.dnamaster10.traincartsticketshop.util.database.accessors.PlayerDataAccessor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import static com.dnamaster10.traincartsticketshop.TraincartsTicketShop.getPlugin;

public class PlayerJoinEventHandler implements Listener {
    private final TraincartsTicketShop plugin;

    public PlayerJoinEventHandler(TraincartsTicketShop plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    void onPlayerJoin(PlayerJoinEvent event) {
        //Update player in database
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            try {
                PlayerDataAccessor accessor = new PlayerDataAccessor();
                Player p = event.getPlayer();
                accessor.updatePlayer(p.getDisplayName(), p.getUniqueId().toString());
            } catch (ModificationException e) {
                getPlugin().handleSqlException(e);
            }
        });

        if (event.getPlayer().hasPermission("traincartsticketshop.updatenotifications") && plugin.isUpdateAvailable()) {
            event.getPlayer().sendMessage(ChatColor.GREEN + "An update for TraincartsTicketShop is available. Download it from here: https://github.com/DNAmaster10/TraincartsTicketShop/releases");
        }
    }
}
