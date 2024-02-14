package com.dnamaster10.traincartsticketshop.util;

import com.dnamaster10.traincartsticketshop.TraincartsTicketShop;
import com.dnamaster10.traincartsticketshop.util.database.PlayerAccessor;
import com.dnamaster10.traincartsticketshop.util.database.databaseobjects.PlayerDatabaseObject;
import com.dnamaster10.traincartsticketshop.util.exceptions.DMLException;
import com.dnamaster10.traincartsticketshop.util.exceptions.DQLException;
import org.bukkit.entity.Player;

import java.io.IOException;

public class Players {
    //Contains utility methods for players
    public static PlayerDatabaseObject getPlayerByUsername(String username) throws DQLException, DMLException {
        //Takes in a username, and returns either null if the player doesn't exist
        //Or returns the corrected player username.
        //First checks online players, then the database, and then the Mojang api as a final fallback

        //Check online players
        for (Player p : TraincartsTicketShop.getPlugin().getServer().getOnlinePlayers()) {
            if (p.getDisplayName().equalsIgnoreCase(username)) {
                return new PlayerDatabaseObject(p.getDisplayName(), p.getUniqueId().toString());
            }
        }

        //Check if the player exists in the database
        PlayerAccessor playerAccessor = new PlayerAccessor();
        if (playerAccessor.checkPlayerByUsername(username)) {
            return playerAccessor.getPlayerByUsername(username);
        }

        //Finally, if the player has not been found in any of the above places, get them from the Mojang API
        //The Mojang API is limited to 600 requests per 10 minutes. As a result, if this is run too frequently, it will
        //not work. Additionally, if the API goes offline, this will also not work.
        //In this event, this method will handle as if the player does not exist.
        MojangApiAccessor apiAccessor = new MojangApiAccessor();
        try {
            String[] playerInfo = apiAccessor.getPlayerFromUsername(username);

            //Register the player in the database to save future API requests
            playerAccessor.updatePlayer(playerInfo[0], playerInfo[1]);
            return new PlayerDatabaseObject(playerInfo[0], playerInfo[1]);
        } catch (IOException e) {
            return null;
        }
    }
}
