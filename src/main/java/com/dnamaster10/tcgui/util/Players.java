package com.dnamaster10.tcgui.util;

import org.bukkit.entity.Player;

import java.sql.SQLException;

public class Players {
    //Player related global methods
    private static void updateOrInsertPlayer(String name, String uuid) throws SQLException {
        //Updates if player exists, insert if not
        if (Database.checkPlayerByUuid(uuid)) {
            Database.updatePlayer(name, uuid);
        }
        else {
            Database.addPlayer(name, uuid);
        }
    }
    public static void updatePlayer(String name, String uuid) throws SQLException {
        updateOrInsertPlayer(name, uuid);
    }
    public static void updatePlayer(Player p) throws SQLException {
        updateOrInsertPlayer(p.getDisplayName(), String.valueOf(p.getUniqueId()));
    }
}
