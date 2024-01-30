package com.dnamaster10.tcgui.util;

import com.dnamaster10.tcgui.objects.ShopGui;
import com.dnamaster10.tcgui.util.database.GuiAccessor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.block.sign.SignSide;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import java.sql.SQLException;
import java.util.Objects;

import static com.dnamaster10.tcgui.TraincartsGui.getPlugin;

public class SignHandler {
    //Sign format example:
    //Line 1: This is a gui!
    //Line 2: Test123
    //Line 3: [tcgui] <optional page num>
    //Line 4: <tcgui name>
    boolean isGuiSign(Sign sign) {
        String signIdentifier = getPlugin().getConfig().getString("SignIdentifier");
        if (signIdentifier == null || signIdentifier.isBlank()) {
            return false;
        }
        SignSide side1 = sign.getSide(Side.FRONT);
        SignSide side2 = sign.getSide(Side.BACK);
        if (side1.getLine(2).contains(signIdentifier)) {
            return true;
        }
        return side2.getLine(2).contains(signIdentifier);
    }
    Side getSignSide(Sign sign) {
        //This method will be replaced in the future with a built-in method from Spigot.
        //Currently, there's not an easy way without some maths to get the side of a sign clicked
        //So this method will just return whichever side contains the tcgui identifier,
        //prioritising side 1 if they both contain a sign identifier.
        //A pull request has been added on the spigot repo to add this.
        String signIdentifier = getPlugin().getConfig().getString("SignIdentifier");
        if (signIdentifier == null || signIdentifier.isBlank()) {
            return null;
        }
        if (sign.getSide(Side.FRONT).getLine(2).contains(signIdentifier)) {
            return Side.FRONT;
        }
        return Side.BACK;
    }
    int getPage(SignSide side) {
        //Defaults to page 0 if none is specified
        String[] args = side.getLine(2).split(" ");
        if (args.length > 1) {
            if (Utilities.isInt(args[1])) {
                //Page must be reduced by one to convert from a regular number to an index
                return Integer.parseInt(args[1]) - 1;
            }
        }
        return 0;
    }
    String getGuiName(SignSide side) {
        String nameLine = side.getLine(3);
        if (nameLine.isBlank()) {
            return null;
        }
        return nameLine;
    }
    public boolean handleSignClickEvent(PlayerInteractEvent event) {
        //Clicked block here will always be a sign as this is checked from
        //the PlayerInteractEventHandler
        //Check if player is crouching. If they are, treat as edit sign.
        if (event.getPlayer().isSneaking()) {
            return false;
        }
        //First check that the sign is a tcgui sign
        Sign sign = (Sign) Objects.requireNonNull(event.getClickedBlock()).getState();
        if (!isGuiSign(sign)) {
            return false;
        }
        //Get sign details
        SignSide side = sign.getSide(getSignSide(sign));
        String guiName = getGuiName(side);
        if (guiName == null) {
            return false;
        }

        //Handle sign click async from here. True will be returned beforehand to cancel the sign edit event.
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            try {
                //Check the gui exists
                GuiAccessor guiAccessor = new GuiAccessor();
                if (!guiAccessor.checkGuiByName(guiName)) {
                    event.getPlayer().sendMessage(ChatColor.RED + "No gui with name \"" + guiName + "\" exists");
                    return;
                }

                //Check the max page number. If the number on the sign is higher than the max pages in the gui, set the page to the highest possible page
                int page = getPage(side);
                int guiId = guiAccessor.getGuiIdByName(guiName);
                int maxPage = guiAccessor.getTotalPages(guiId);
                if (page > maxPage) {
                    page = maxPage;
                }
                if (page < 0) {
                    page = 0;
                }

                //Create new gui
                Player p = event.getPlayer();
                ShopGui gui = new ShopGui(guiName, page, p);

                //Open the gui
                gui.open();

                //Clear old player guis
                getPlugin().getGuiManager().clearGuis(p);

                //Register the gui
                getPlugin().getGuiManager().addGui(p, gui);
            } catch (SQLException e) {
                getPlugin().reportSqlError(event.getPlayer(), e.toString());
            }
        });
        return  true;
    }
}
