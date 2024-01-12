package com.dnamaster10.tcgui.util;

import com.dnamaster10.tcgui.TraincartsGui;
import com.dnamaster10.tcgui.objects.Gui;
import com.dnamaster10.tcgui.util.database.GuiAccessor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class Guis {
    //For methods relating to guis
    public static void createGuiCommand(Player p, String guiName) {
        //Takes in a command, creates a gui
        //Open async thread for database queries
        Bukkit.getScheduler().runTaskAsynchronously(TraincartsGui.plugin, () -> {
            GuiAccessor guiAccessor;
            try {
                guiAccessor = new GuiAccessor();
            } catch (SQLException e) {
                TraincartsGui.plugin.reportSqlError(p, e.toString());
                return;
            }
            //Check GUI doesn't already exist
            try {
                if (guiAccessor.checkGuiByName(guiName)) {
                    p.sendMessage(ChatColor.RED + "A gui with the name \"" + guiName + "\" already exists");
                    return;
                }
            } catch (SQLException e) {
                //Database error, cancel creation
                TraincartsGui.plugin.reportSqlError(p, e.toString());
                return;
            }
            //Gui does not already exist, proceed
            String uuid = String.valueOf(p.getUniqueId());

            //Finally register the gui in the database
            try {
                guiAccessor.addGui(guiName, uuid);
            } catch (SQLException e) {
                TraincartsGui.plugin.reportSqlError(p, e.toString());
                return;
            }
            p.sendMessage(ChatColor.GREEN + "A gui with the name \"" + guiName + "\" was created");
        });
    }
    public static void editGuiCommand(Player p, String guiName) {
        //Opens a gui for editing
        Bukkit.getScheduler().runTaskAsynchronously(TraincartsGui.plugin, () -> {
            GuiAccessor guiAccessor;
            try {
                guiAccessor = new GuiAccessor();
            } catch (SQLException e) {
                TraincartsGui.plugin.reportSqlError(p, e.toString());
                return;
            }
            //Check that gui exists
            try {
                if (guiAccessor.checkGuiByName(guiName)) {
                    p.sendMessage(ChatColor.RED + "No gui with name \"" + guiName + "\" exists");
                    return;
                }
            } catch (SQLException e) {
                TraincartsGui.plugin.reportSqlError(p, e.toString());
                return;
            }
            //Gui exists in database, proceed
            //Check that player is either owner or editor
            try {
                boolean hasPermission = guiAccessor.checkGuiOwnershipByUuid(guiName, p.getUniqueId().toString()) || guiAccessor.checkGuiEditByUuid(guiName, p.getUniqueId().toString());
                if (!hasPermission) {
                    p.sendMessage(ChatColor.RED + "You do not have permission to edit that gui");
                    return;
                }
            } catch (SQLException e) {
                TraincartsGui.plugin.reportSqlError(p, e.toString());
                return;
            }
            //Player has edit perms, create gui object
            Gui gui = new Gui(guiName);
        });
    }
}
