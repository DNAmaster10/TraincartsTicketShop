package com.dnamaster10.tcgui;

import com.dnamaster10.tcgui.util.Database;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

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
}
