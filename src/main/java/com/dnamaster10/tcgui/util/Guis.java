package com.dnamaster10.tcgui.util;

import com.dnamaster10.tcgui.TraincartsGui;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class Guis {
    //For methods relating to guis
    public static void createGui() {

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

            //Check if player exists in database. If not, insert player name
            String username = null;
            try {
                username = Database.getUsernameFromUuid(uuid);
            } catch (SQLException e) {
                //Database error, cancel creation
                TraincartsGui.plugin.reportSqlError(p, e.toString());
            }
        });
    }
}
