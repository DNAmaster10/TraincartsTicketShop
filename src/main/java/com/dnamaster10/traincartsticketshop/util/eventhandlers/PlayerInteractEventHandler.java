package com.dnamaster10.traincartsticketshop.util.eventhandlers;

import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import static com.dnamaster10.traincartsticketshop.TraincartsTicketShop.getPlugin;

public class PlayerInteractEventHandler implements Listener {
    //Used for signs
    @EventHandler(ignoreCancelled = true)
    void onPlayerInteract(PlayerInteractEvent event) {
        //Check that the player clicked a block
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        //Check that the block is a sign
        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }
        if (!(block.getState() instanceof Sign)) {
            return;
        }
        event.setCancelled(getPlugin().getSignHandler().handleSignClickEvent(event));
    }
}
