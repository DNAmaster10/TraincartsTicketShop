package com.dnamaster10.tcgui.commands.commandhandlers.gui;

import com.dnamaster10.tcgui.commands.commandhandlers.CommandHandler;
import com.dnamaster10.tcgui.util.database.GuiAccessor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.StringJoiner;

public class GuiSetDisplayNameCommandHandler extends CommandHandler<SQLException> {
    //Example command: /tcgui gui setdisplayname <gui name> <gui display name>
    private String rawDisplayName;
    private String colouredDisplayName;
    @Override
    protected boolean checkSync(CommandSender sender, String[] args) {
        //This command can be run by the player as well as other interfaces

        //Check config
        if (!getPlugin().getConfig().getBoolean("AllowGuiSetDisplayName")) {
            returnError(sender, "Changing gui display names is disabled on this server");
            return false;
        }

        //Check permissions
        if (sender instanceof Player p) {
            if (!p.hasPermission("tcgui.gui.setdisplayname") && !p.hasPermission("tcgui.admin.gui.setdisplayname")) {
                returnError(sender, "You do not have permission to perform that action");
                return false;
            }
        }

        //Check syntax
        if (args.length < 4) {
            returnError(sender, "Missing argument(s): /tcgui setDisplayName <gui name> <gui display name>");
            return false;
        }

        //Check gui name to save on database calls
        if (!checkGuiNameSyntax(args[2])) {
            returnGuiNotFoundError(sender, args[2]);
            return false;
        }

        //Build display name
        StringJoiner stringJoiner = new StringJoiner(" ");
        for (int i = 3; i < args.length; i++) {
            stringJoiner.add(args[i]);
        }
        colouredDisplayName = ChatColor.translateAlternateColorCodes('&', stringJoiner.toString());
        rawDisplayName = ChatColor.stripColor(colouredDisplayName);

        if (rawDisplayName.length() > 25) {
            returnError(sender, "Gui display names cannot be more than 20 characters in length");
            return false;
        }
        if (rawDisplayName.isBlank()) {
            returnError(sender, "Gui display names cannot be less than 1 character in length");
            return false;
        }
        return true;
    }

    @Override
    protected boolean checkAsync(CommandSender sender, String[] args) throws SQLException {
        //Check gui exists
        GuiAccessor guiAccessor = new GuiAccessor();
        if (!guiAccessor.checkGuiByName(args[2])) {
            returnGuiNotFoundError(sender, args[2]);
            return false;
        }

        //If sender is player, check that player is an editor of that gui
        if (sender instanceof Player p) {
            if (!p.hasPermission("tcgui.admin.gui.setdisplayname")) {
                if (!guiAccessor.playerCanEdit(args[2], p.getUniqueId().toString())) {
                    returnError(sender, "You do not have permission to edit that gui. Request that the owner adds you as an editor before making any changes");
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    protected void execute(CommandSender sender, String[] args) throws SQLException {
        GuiAccessor guiAccessor = new GuiAccessor();
        guiAccessor.updateGuiDisplayName(args[2], colouredDisplayName, rawDisplayName);
        sender.sendMessage(ChatColor.GREEN + "Gui \"" + args[2] + "\"'s display name was changed to \"" + colouredDisplayName + "\"");
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        if (!checkSync(sender, args)) {
            return;
        }
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            try {
                if (!checkAsync(sender, args)) {
                    return;
                }
                execute(sender, args);
            } catch (SQLException e) {
                getPlugin().reportSqlError(sender, e.toString());
            }
        });
    }
}
