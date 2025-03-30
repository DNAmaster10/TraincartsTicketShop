package com.dnamaster10.traincartsticketshop;

import com.dnamaster10.traincartsticketshop.brigadier.TicketShopCommands;
import com.dnamaster10.traincartsticketshop.util.*;
import com.dnamaster10.traincartsticketshop.util.eventhandlers.*;
import com.dnamaster10.traincartsticketshop.util.exceptions.ModificationException;
import com.dnamaster10.traincartsticketshop.util.exceptions.QueryException;
import com.dnamaster10.traincartsticketshop.util.database.DatabaseAccessorFactory;
import com.dnamaster10.traincartsticketshop.util.database.accessors.DataAccessor;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public final class TraincartsTicketShop extends JavaPlugin implements Listener {
    public static TraincartsTicketShop plugin;

    private GuiManager guiManager;
    /**
     * @return The gui manager currently in use.
     * @see GuiManager
     */
    public GuiManager getGuiManager() {
        return this.guiManager;
    }

    private SignHandler signHandler;
    /**
     * @return The sign handler current in use.
     * @see SignHandler
     */
    public SignHandler getSignHandler() {
        return this.signHandler;
    }

    /**
     * @return This class instance
     */
    public static TraincartsTicketShop getPlugin() {
        return plugin;
    }

    private VaultHook vaultHook;

    /**
     * @return The Vault Hook
     */
    public VaultHook getVaultHook() {
        return vaultHook;
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
            ConfigUtils.migrateIfNeeded(getConfig(), this);
        } catch (IOException e) {
            getLogger().severe("Failed to update config values");
            e.printStackTrace();
        }

        //Create database tables
        try {
            DatabaseAccessorFactory.getDatabaseTableCreator().createTables();
        } catch (ModificationException e) {
            //Disable plugin if failed
            plugin.handleSqlException(e);
        }

        //Initialize database caches
        try {
            new DataAccessor().initializeCaches();
        } catch (QueryException e) {
            plugin.handleSqlException(e);
            getPlugin().disable();
        }

        //Register commands
        List<String> aliases = new ArrayList<>();
        aliases.add("tshop");
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> commands.registrar().register(TicketShopCommands.getRootNode(), "The root Ticket Shop command", aliases));

        //Register gui manager
        this.guiManager = new GuiManager();

        //Register the sign handler
        this.signHandler = new SignHandler();

        //Register listeners
        getServer().getPluginManager().registerEvents(new InventoryCloseEventHandler(), plugin);
        getServer().getPluginManager().registerEvents(new InventoryClickEventHandler(), plugin);
        getServer().getPluginManager().registerEvents(new InventoryDragEventHandler(), plugin);
        getServer().getPluginManager().registerEvents(new PlayerJoinEventHandler(), plugin);
        getServer().getPluginManager().registerEvents(new PlayerQuitEventHandler(), plugin);
        getServer().getPluginManager().registerEvents(new PlayerInteractEventHandler(), plugin);

        //Register BStats metrics class
        int pluginId = 23289;
        Metrics metrics = new Metrics(this, pluginId);

        //Schedule economy load
        this.vaultHook = new VaultHook();
        if (getConfig().getBoolean("UseEconomy")) {
            Bukkit.getScheduler().runTaskLater(this, () -> vaultHook.loadEconomy(), 1L);

            //Check config values
            double defaultPrice = getConfig().getDouble("DefaultTicketPrice");

            if (defaultPrice < 0) {
                getLogger().severe("The default ticket price cannot be negative.");
                disable();
            }

            if (getConfig().getBoolean("AllowCustomTicketPrices")) {
                double minimum = getConfig().getDouble("MinTicketPrice");
                double maximum = getConfig().getDouble("MaxTicketPrice");

                if (defaultPrice < minimum) {
                    getLogger().severe("The default ticket price cannot be less than the minimum ticket price.");
                    disable();
                } else if (defaultPrice > maximum) {
                    getLogger().severe("The default ticket price cannot be more than the maximum ticket price.");
                    disable();
                } else if (minimum < 0) {
                    getLogger().severe("The minimum ticket price cannot be negative.");
                    disable();
                } else if (maximum < 0) {
                    getLogger().severe("The maximum ticket price cannot be negative.");
                    disable();
                } else if (maximum < minimum) {
                    getLogger().severe("The maximum ticket price cannot be less than the minimum ticket price.");
                    disable();
                }
            }
        } else {
            getLogger().info("Economy support is disabled in the config, so economy features have been disabled.");
        }

        plugin.getLogger().info("TraincartsTicketShop has finished loading!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    /**
     * Called to handle any database exceptions.
     *
     * @param e The SQLException
     * @see ModificationException
     * @see QueryException
     */
    public void handleSqlException(SQLException e) {
        //If the exception was only a query, report it. If it altered the database, disable the plugin too as a failsafe.
        plugin.getLogger().severe("A database error occurred: " + e);
        e.printStackTrace();
        if (e instanceof QueryException) {
            return;
        }
        if (plugin.getConfig().getBoolean("DisableOnDatabaseModificationException")) {
            disable();
        }
    }

    /**
     * Called to handle any database exceptions.
     * Will also return a short message to the sender indicating that an error occurred.
     *
     * @param sender The CommandSender
     * @param e The SQLException
     * @see ModificationException
     * @see QueryException
     */
    public void handleSqlException(CommandSender sender, SQLException e) {
        sender.sendMessage(Utilities.parseColour("<red>A database error occurred. Check server logs for more info."));
        handleSqlException(e);
    }

    /**
     * Disables the plugin. Note that EditGuis do not need to be saved here, as shutting down the server also triggers
     * the inventory close event, automatically saving all changes.
     */
    public void disable() {
        //Disables the plugin. For use when a severe error occurs
        plugin.getLogger().info("Disabling TraincartsTicketShop...");
        plugin.getServer().getPluginManager().disablePlugin(this);
    }
}
