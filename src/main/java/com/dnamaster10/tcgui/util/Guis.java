package com.dnamaster10.tcgui.util;

import com.dnamaster10.tcgui.TraincartsGui;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class Guis {
    //For methods relating to guis
    public static void createGui(String name, String ownerUuid) throws SQLException {
        //Adds a new gui in the database
        //Checks for gui with same name must be done before calling this method
        Database.addGui(name, ownerUuid);
    }
    public static void createGuiCommand(Player p, String name) {
        //Takes in a command, creates a gui
        //Open async thread for database queries
        Bukkit.getScheduler().runTaskAsynchronously(TraincartsGui.plugin, () -> {
            //Check GUI doesn't already exist
            try {
                if (Database.checkGuiByName(name)) {
                    p.sendMessage(ChatColor.RED + "A gui with the name \"" + name + "\" already exists");
                    return;
                }
            } catch (SQLException e) {
                //Database error, cancel creation
                TraincartsGui.plugin.reportSqlError(p, e.toString());
                return;
            }
            //Gui does not already exist, proceed
            String uuid = String.valueOf(p.getUniqueId());

            //If the player needs updating in the database, update them
            try {
                Players.updatePlayer(p);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            //Finally register the gui in the database
            try {
                createGui(name, uuid);
            } catch (SQLException e) {
                TraincartsGui.plugin.reportSqlError(p, e.toString());
                return;
            }
            p.sendMessage(ChatColor.GREEN + "A gui with the name \"" + name + "\" was created");
        });
    }
}
