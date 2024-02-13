package com.dnamaster10.traincartsticketshop.util;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class ConfigUtil {
    public static void migrateIfNeeded(FileConfiguration oldConfig, Plugin plugin) throws IOException {
        if (!Objects.equals(oldConfig.getString("ConfigVersion"), plugin.getDescription().getVersion()) || plugin.getDescription().getVersion().contains("SNAPSHOT")) {
            plugin.getLogger().info("Your config version does not match the plugin version (or you are using a dev build), updating...");

            //load config
            File oldConfigFile = new File(plugin.getDataFolder(), "config.yml");

            //put values in map
            Scanner oldConfigReader = new Scanner(oldConfigFile);
            Map<String, String> oldConfigMap = new HashMap<>();
            while (oldConfigReader.hasNextLine()) {
                final String line = oldConfigReader.nextLine();
                if (line.startsWith("#")) continue;
                final String[] split = line.split(":");
                if (split.length == 1) continue;
                oldConfigMap.put(split[0], String.join(":", Arrays.copyOfRange(split, 1, split.length)).trim());
            }
            oldConfigReader.close();

            //load new config
            oldConfigFile.delete();
            plugin.saveDefaultConfig();

            File newConfigFile = new File(plugin.getDataFolder(), "config.yml");

            //change values where necessary
            Scanner newConfigReader = new Scanner(newConfigFile);
            final List<String> newConfigLines = new ArrayList<>();
            while (newConfigReader.hasNextLine()) {
                final String line = newConfigReader.nextLine();
                newConfigLines.add(line);
                if (line.startsWith("config-version") || line.startsWith("#")) continue;
                final String[] split = line.split(":");
                if (split.length == 1) continue;
                if (oldConfigMap.containsKey(split[0])) {
                    split[1] = oldConfigMap.get(split[0]);
                    newConfigLines.set(newConfigLines.size() - 1, split[0] + ": " + split[1]);
                    plugin.getLogger().info("Migrated config option " + split[0] + " with value " + split[1]);
                }
            }
            final String newConfig = String.join(System.lineSeparator(), newConfigLines);
            FileWriter fileWriter = new FileWriter(new File(plugin.getDataFolder(), "config.yml"));
            fileWriter.write(newConfig);
            fileWriter.close();

            plugin.reloadConfig();
        }
    }
}
