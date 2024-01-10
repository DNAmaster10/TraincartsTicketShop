package com.dnamaster10.tcgui;

import com.dnamaster10.tcgui.util.Database;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
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
        Commands.execute(sender, command, label, args);
        return true;
    }
    public void reportSqlError(String error) {
        plugin.getLogger().severe("A database error occurred: " + error);
    }
    public void reportSqlError(Player p, String error) {
        p.sendMessage(ChatColor.RED + "An error occurred. Check server logs for more info");
        plugin.getLogger().severe("A database error occurred: " + error);
    }
}
