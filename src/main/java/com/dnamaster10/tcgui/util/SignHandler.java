package com.dnamaster10.tcgui.util;

import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.block.sign.SignSide;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import static com.dnamaster10.tcgui.TraincartsGui.getPlugin;

public class SignHandler {
    boolean isGuiSign(Sign sign) {
        String signIdentifier = getPlugin().getConfig().getString("SignIdentifier");
        if (signIdentifier == null || signIdentifier.isBlank()) {
            return false;
        }
        SignSide side1 = sign.getSide(Side.FRONT);
        SignSide side2 = sign.getSide(Side.BACK);
        getPlugin().getLogger().severe("Sign text: " + side1.getLine(4));
        if (side1.getLine(3).contains(signIdentifier)) {
            return true;
        }
        return side2.getLine(3).contains(signIdentifier);
    }
    public boolean handleSignClickEvent(PlayerInteractEvent event) {
        //First check that the sign is a tcgui sign
        Sign sign = (Sign) event.getClickedBlock().getState();
        isGuiSign(sign);
        return  false;
    }
}
