package com.dnamaster10.tcgui;

import com.dnamaster10.tcgui.commands.CommandDispatcher;
import com.dnamaster10.tcgui.commands.tabcompleters.GuiTabCompleter;
import com.dnamaster10.tcgui.commands.tabcompleters.TabCompleter;
import com.dnamaster10.tcgui.util.Players;
import com.dnamaster10.tcgui.util.database.DatabaseConfig;
import com.dnamaster10.tcgui.util.database.TableCreator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;


public final class TraincartsGui extends JavaPlugin implements Listener {
    public static TraincartsGui plugin;
    @Override
    public void onEnable() {
        //Get plugin
        plugin = this;
        plugin.getLogger().info("Staring TraincartsGui...");

        //Load config file
        getConfig().options().copyDefaults();
        saveDefaultConfig();

        //Configure database
        DatabaseConfig.setUrl(plugin.getConfig().getString("database.host"), plugin.getConfig().getString("database.port"), plugin.getConfig().getString("database.database"));
        DatabaseConfig.setUsername(plugin.getConfig().getString("database.user"));
        DatabaseConfig.setPassword(plugin.getConfig().getString("database.password"));

        //Create tables in database
        try {
            TableCreator tableCreator = new TableCreator();
            tableCreator.createTables();
        }
        catch (SQLException e) {
            //Disable plugin if failed
            plugin.reportSqlError("Failed to create tables in database: " + String.valueOf(e));
            plugin.disable();
        }
        //Register the "tcgui" command
        Objects.requireNonNull(getCommand("tcgui")).setExecutor(new CommandDispatcher());

        //Register tab completers
        Objects.requireNonNull(getCommand("tcgui")).setTabCompleter(new TabCompleter());

        plugin.getLogger().info("TraincartsGui has finished loading!");
    }


    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        //Update player in database
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                Players.updatePlayer(event.getPlayer());
            } catch (SQLException e) {
                reportSqlError(e.toString());
            }
        });
    }
    public void reportSqlError(String error) {
        plugin.getLogger().severe("A database error occurred: " + error);
    }
    public void reportSqlError(Player p, String error) {
        p.sendMessage(ChatColor.RED + "An error occurred. Check server logs for more info");
        plugin.getLogger().severe("A database error occurred: " + error);
    }
    public void disable() {
        //Disables the plugin. For use when a severe error occurs
        plugin.getLogger().info("Disabling TraincartsGui...");
        plugin.getServer().getPluginManager().disablePlugin(this);
    }
}
