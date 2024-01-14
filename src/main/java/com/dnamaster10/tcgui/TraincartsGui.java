package com.dnamaster10.tcgui;

import com.dnamaster10.tcgui.commands.CommandDispatcher;
import com.dnamaster10.tcgui.commands.tabcompleters.TabCompleter;
import com.dnamaster10.tcgui.util.GuiManager;
import com.dnamaster10.tcgui.util.database.DatabaseConfig;
import com.dnamaster10.tcgui.util.database.TableCreator;
import com.dnamaster10.tcgui.util.eventhandlers.InventoryClickEventHandler;
import com.dnamaster10.tcgui.util.eventhandlers.InventoryCloseEventHandler;
import com.dnamaster10.tcgui.util.eventhandlers.PlayerJoinEventHandler;
import com.dnamaster10.tcgui.util.eventhandlers.PlayerQuitEventHandler;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.Objects;


public final class TraincartsGui extends JavaPlugin implements Listener {
    public static TraincartsGui plugin;
    private GuiManager guiManager;
    public static TraincartsGui getPlugin() {
        return plugin;
    }
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

        //Register gui manager
        guiManager = new GuiManager();

        //Register listeners
        getServer().getPluginManager().registerEvents(new InventoryClickEventHandler(), plugin);
        getServer().getPluginManager().registerEvents(new InventoryCloseEventHandler(), plugin);
        getServer().getPluginManager().registerEvents(new PlayerJoinEventHandler(), plugin);
        getServer().getPluginManager().registerEvents(new PlayerQuitEventHandler(), plugin);

        plugin.getLogger().info("TraincartsGui has finished loading!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public GuiManager getGuiManager() {
        return guiManager;
    }
    public void reportSqlError(String error) {
        plugin.getLogger().severe("A database error occurred: " + error);
    }
    public void reportSqlError(Player p, String error) {
        p.sendMessage(ChatColor.RED + "An error occurred. Check server logs for more info");
        plugin.getLogger().severe("A database error occurred: " + error);
        //Disable the plugin in case of database damage
        plugin.disable();
    }
    public void reportSqlError(CommandSender sender, String error) {
        if (sender instanceof Player p) {
            reportSqlError(p, error);
        }
        else if (sender instanceof ConsoleCommandSender) {
            plugin.getLogger().severe("A database error occurred: " + error);
            //Disable the plugin in case of database damage
            plugin.disable();
        }
    }
    public void disable() {
        //Disables the plugin. For use when a severe error occurs
        plugin.getLogger().info("Disabling TraincartsGui...");
        plugin.getServer().getPluginManager().disablePlugin(this);
    }
}
