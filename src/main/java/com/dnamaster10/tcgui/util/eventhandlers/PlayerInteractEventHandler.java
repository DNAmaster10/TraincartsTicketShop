package com.dnamaster10.tcgui.util.eventhandlers;

import com.dnamaster10.tcgui.TraincartsGui;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Objects;

public class PlayerInteractEventHandler implements Listener {
    //Used for signs
    @EventHandler
    void onPlayerInteract(PlayerInteractEvent event) {
        //Check that the player clicked a block
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        //Check that the block is a sign
        if (event.getClickedBlock().getState() instanceof Sign) {
            //Block is a sign, handle sign click
            //TraincartsGui.getPlugin().getSignHandler()
        }
    }
}
