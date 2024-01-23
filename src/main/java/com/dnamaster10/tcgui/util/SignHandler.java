package com.dnamaster10.tcgui.util;

import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.block.sign.SignSide;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import static com.dnamaster10.tcgui.TraincartsGui.getPlugin;

public class SignHandler {
    boolean isGuiSign(Sign sign) {
        String signIdentifier = getPlugin().getConfig().getString("SignIdentifier");
        //SignSide side = sign.
        return false;
    }
    public boolean handleSignClickEvent(PlayerInteractEvent event) {
        //First check that the sign is a tcgui sign
        //Sign sign = (Sign) block.getState();
        //String bottomText = sign.get
        return  false;
    }
}
