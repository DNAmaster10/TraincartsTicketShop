package com.dnamaster10.tcgui.util;

import com.dnamaster10.tcgui.util.database.PlayerAccessor;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class Players {
    //Player related global methods
    private static void updateOrInsertPlayer(String name, String uuid) throws SQLException {
        //Get database accessor
        PlayerAccessor playerAccessor = new PlayerAccessor();
        //Updates if player exists, insert if not
        if (playerAccessor.checkPlayerByUuid(uuid)) {
            playerAccessor.updatePlayer(name, uuid);
        }
        else {
            playerAccessor.addPlayer(name, uuid);
        }
    }
    public static void updatePlayer(String name, String uuid) throws SQLException {
        updateOrInsertPlayer(name, uuid);
    }
    public static void updatePlayer(Player p) throws SQLException {
        updateOrInsertPlayer(p.getDisplayName(), String.valueOf(p.getUniqueId()));
    }
}
