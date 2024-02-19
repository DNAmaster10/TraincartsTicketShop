package com.dnamaster10.traincartsticketshop;

import com.dnamaster10.traincartsticketshop.commands.CommandDispatcher;
import com.dnamaster10.traincartsticketshop.commands.TabCompleter;
import com.dnamaster10.traincartsticketshop.util.ConfigUtil;
import com.dnamaster10.traincartsticketshop.util.GuiManager;
import com.dnamaster10.traincartsticketshop.util.SignHandler;
import com.dnamaster10.traincartsticketshop.util.database.DatabaseConfig;
import com.dnamaster10.traincartsticketshop.util.database.TableCreator;
import com.dnamaster10.traincartsticketshop.util.eventhandlers.*;
import com.dnamaster10.traincartsticketshop.util.exceptions.DMLException;
import com.dnamaster10.traincartsticketshop.util.exceptions.DQLException;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;


public final class TraincartsTicketShop extends JavaPlugin implements Listener {
    public static TraincartsTicketShop plugin;
    private GuiManager guiManager;
    public GuiManager getGuiManager() {
        return this.guiManager;
    }
    private SignHandler signHandler;
    public SignHandler getSignHandler() {
        return this.signHandler;
    }
    public static TraincartsTicketShop getPlugin() {
        return plugin;
    }
    @Override
    public void onEnable() {
        //Get plugin
        plugin = this;
        plugin.getLogger().info("Starting TraincartsTicketShop...");

        //Load config file
        getConfig().options().copyDefaults();
        saveDefaultConfig();

        // Update config stuff if needed
        try {
            ConfigUtil.migrateIfNeeded(getConfig(), this);
        } catch (IOException e) {
            getLogger().severe("Failed to update config values");
            e.printStackTrace();
        }

        //Configure database
        DatabaseConfig.setUrl(plugin.getConfig().getString("database.host"), plugin.getConfig().getString("database.port"), plugin.getConfig().getString("database.database"));
        DatabaseConfig.setUsername(plugin.getConfig().getString("database.user"));
        DatabaseConfig.setPassword(plugin.getConfig().getString("database.password"));

        //Create tables in database
        try {
            TableCreator tableCreator = new TableCreator();
            tableCreator.createTables();
        }
        catch (DQLException | DMLException e) {
            //Disable plugin if failed
            plugin.handleSqlException(e);
        }
        //Register the "traincartsticketshop" command
        Objects.requireNonNull(getCommand("traincartsticketshop")).setExecutor(new CommandDispatcher());

        //Register tab completers
        Objects.requireNonNull(getCommand("traincartsticketshop")).setTabCompleter(new TabCompleter());

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

        plugin.getLogger().info("TraincartsTicketShop has finished loading!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
    public void handleSqlException(SQLException e) {
        //If the exception was only a query, report it. If it altered the database, disable the plugin too as a failsafe.
        plugin.getLogger().severe("A database error occurred: " + e);
        e.printStackTrace();
        if (e instanceof DQLException) {
            return;
        }
        disable();
    }
    public void handleSqlException(CommandSender sender, SQLException e) {
        sender.sendMessage(ChatColor.RED + "A database error occurred. Check server logs for more info");
        handleSqlException(e);
    }
    public void disable() {
        //Disables the plugin. For use when a severe error occurs
        plugin.getLogger().info("Disabling TraincartsTicketShop...");
        plugin.getServer().getPluginManager().disablePlugin(this);
    }
}
