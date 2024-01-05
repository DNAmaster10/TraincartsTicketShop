package com.dnamaster10.tcgui;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class TraincartsGui extends JavaPlugin {
    TraincartsGui p;
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
