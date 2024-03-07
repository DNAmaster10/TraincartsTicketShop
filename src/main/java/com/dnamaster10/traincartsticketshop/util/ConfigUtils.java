package com.dnamaster10.traincartsticketshop.util;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class ConfigUtils {
    public static void migrateIfNeeded(FileConfiguration oldConfig, Plugin plugin) throws IOException {
        if (!Objects.equals(oldConfig.getString("ConfigVersion"), plugin.getDescription().getVersion()) || plugin.getDescription().getVersion().contains("SNAPSHOT")) {
            plugin.getLogger().info("Your config version does not match the plugin version (or you are using a dev build), updating...");

            //load config
            File oldConfigFile = new File(plugin.getDataFolder(), "config.yml");

            //put values in map

            //this is for beginning of multilines but idk how to des
            String oldMultilineStartValue = "";

            Scanner oldConfigReader = new Scanner(oldConfigFile);
            Map<String, String> oldConfigMap = new HashMap<>();
            Map<String, Map<String, String>> oldConfigMultilineOptions = new HashMap<>();

            while (oldConfigReader.hasNextLine()) {
                final String line = oldConfigReader.nextLine();
                if (line.startsWith("#")) continue;
                final String[] split = line.split(":");
                if (split.length == 1) { // Chance it might be the beginning of a multiline/nested option
                    if (!line.contains(":") || !split[0].trim().equals(split[0])) continue;
                    oldMultilineStartValue = split[0];
                    oldConfigMultilineOptions.put(oldMultilineStartValue, new HashMap<>());
                } else if (!oldMultilineStartValue.equals("") && !split[0].startsWith(split[0].trim()))
                    oldConfigMultilineOptions.get(oldMultilineStartValue).put(split[0], split[1].trim());
                else oldConfigMap.put(split[0], String.join(":", Arrays.copyOfRange(split, 1, split.length)).trim());
            }
            oldConfigReader.close();

            //load new config
            oldConfigFile.delete();
            plugin.saveDefaultConfig();

            File newConfigFile = new File(plugin.getDataFolder(), "config.yml");

            String newMultilineStartValue = "";

            //change values where necessary
            Scanner newConfigReader = new Scanner(newConfigFile);
            final List<String> newConfigLines = new ArrayList<>();
            while (newConfigReader.hasNextLine()) {
                final String line = newConfigReader.nextLine();
                newConfigLines.add(line);
                if (line.startsWith("ConfigVersion") || line.startsWith("#")) continue;
                final String[] split = line.split(":");
                if (split.length == 1) {
                    if (!line.contains(":")) continue;
                    newMultilineStartValue = split[0];
                } else if (oldConfigMap.containsKey(split[0])) {
                    split[1] = oldConfigMap.get(split[0]);
                    newConfigLines.set(newConfigLines.size() - 1, split[0] + ": " + split[1]);
                    plugin.getLogger().info("Migrated config option " + split[0] + " with value " + split[1]);
                } else if (oldConfigMultilineOptions.containsKey(newMultilineStartValue) && oldConfigMultilineOptions.get(newMultilineStartValue).containsKey(split[0])) {
                    split[1] = oldConfigMultilineOptions.get(newMultilineStartValue).get(split[0]);
                    newConfigLines.set(newConfigLines.size() - 1, split[0] + ": " + split[1]);
                    plugin.getLogger().info("Migrated nested config option " + split[0].trim() + " in " + newMultilineStartValue + " with value " + split[1]);
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
