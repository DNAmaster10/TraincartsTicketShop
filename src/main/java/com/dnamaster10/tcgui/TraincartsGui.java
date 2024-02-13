package com.dnamaster10.tcgui;

import com.dnamaster10.tcgui.commands.CommandDispatcher;
import com.dnamaster10.tcgui.commands.TabCompleter;
import com.dnamaster10.tcgui.util.GuiManager;
import com.dnamaster10.tcgui.util.SignHandler;
import com.dnamaster10.tcgui.util.database.DatabaseConfig;
import com.dnamaster10.tcgui.util.database.TableCreator;
import com.dnamaster10.tcgui.util.eventhandlers.*;
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
    public GuiManager getGuiManager() {
        return this.guiManager;
    }
    private SignHandler signHandler;
    public SignHandler getSignHandler() {
        return this.signHandler;
    }
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
            plugin.reportSqlError(e);
            plugin.disable();
        }
        //Register the "tcgui" command
        Objects.requireNonNull(getCommand("tcgui")).setExecutor(new CommandDispatcher());

        //Register tab completers
        Objects.requireNonNull(getCommand("tcgui")).setTabCompleter(new TabCompleter());

        //Register gui manager
        this.guiManager = new GuiManager();

        //Register the sign handler
        this.signHandler = new SignHandler();

        //Register listeners
        getServer().getPluginManager().registerEvents(new InventoryCloseEventHandler(), plugin);
        getServer().getPluginManager().registerEvents(new InventoryClickEventHandler(), plugin);
        getServer().getPluginManager().registerEvents(new PlayerJoinEventHandler(), plugin);
        getServer().getPluginManager().registerEvents(new PlayerQuitEventHandler(), plugin);
        getServer().getPluginManager().registerEvents(new PlayerInteractEventHandler(), plugin);

        plugin.getLogger().info("TraincartsGui has finished loading!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
    public void reportSqlError(SQLException e) {
        plugin.getLogger().severe("A database error occurred: " + e);
        e.printStackTrace();
    }
    public void reportSqlError(Player p, SQLException e) {
        p.sendMessage(ChatColor.RED + "An error occurred. Check server logs for more info");
        plugin.getLogger().severe("A database error occurred: " + e);
        e.printStackTrace();
        //Disable the plugin in case of database damage
        plugin.disable();
    }
    public void reportSqlError(CommandSender sender, SQLException e) {
        if (sender instanceof Player p) {
            reportSqlError(p, e);
        }
        else if (sender instanceof ConsoleCommandSender) {
            plugin.getLogger().severe("A database error occurred: " + e.toString());
            e.printStackTrace();
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
