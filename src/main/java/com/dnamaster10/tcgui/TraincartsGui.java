package com.dnamaster10.tcgui;

import com.dnamaster10.tcgui.util.Database;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;


public final class TraincartsGui extends JavaPlugin {
    public static TraincartsGui plugin;
    @Override
    public void onEnable() {
        //Get plugin
        plugin = this;
        plugin.getLogger().info("Staring TraincartsGui...");
        if (!Database.checkConnection()) {
            disable();
        }
        //Create tables in database
        Database.createTables();

        plugin.getLogger().info("TraincartsGui has finished loading!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
    public void disable() {
        //Disables the plugin. For use when a severe error occurs
        plugin.getLogger().info("Disabling TraincartsGui...");
        plugin.getServer().getPluginManager().disablePlugin(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Commands.checkCommand(sender, command, label, args);
        return super.onCommand(sender, command, label, args);
    }
}
