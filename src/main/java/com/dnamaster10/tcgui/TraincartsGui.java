package com.dnamaster10.tcgui;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public final class TraincartsGui extends JavaPlugin {
    TraincartsGui p;
    private Connection =
    @Override
    public void onEnable() {
        //Get plugin
        p = this;
        p.getLogger().info("Staring TraincartsGui...");

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
