package com.dnamaster10.traincartsticketshop.commands.commandhandlers.gui;

import com.dnamaster10.traincartsticketshop.commands.commandhandlers.AsyncCommandHandler;
import com.dnamaster10.traincartsticketshop.util.database.mariadb.MariaDBGuiAccessor;
import com.dnamaster10.traincartsticketshop.util.exceptions.ModificationException;
import com.dnamaster10.traincartsticketshop.util.exceptions.QueryException;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.StringJoiner;

public class SetGuiDisplayNameCommandHandler extends AsyncCommandHandler {
    //Example command: /traincartsticketshop gui setdisplayname <gui name> <gui display name>
    private String rawDisplayName;
    private String colouredDisplayName;
    private MariaDBGuiAccessor guiAccessor;
    private Integer guiId;
    @Override
    protected boolean checkSync(CommandSender sender, String[] args) {
        //Check permissions
        if (sender instanceof Player p) {
            if (!p.hasPermission("traincartsticketshop.gui.setdisplayname") && !p.hasPermission("traincartsticketshop.admin.gui.setdisplayname")) {
                returnInsufficientPermissionsError(sender);
                return false;
            }
        }

        //Check syntax
        if (args.length < 4) {
            returnMissingArgumentsError(sender, "/tshop setDisplayName <gui name> <gui display name>");
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
        if (colouredDisplayName.length() > 100) {
            returnError(sender, "Too many colours used in display name");
            return false;
        }
        return true;
    }

    @Override
    protected boolean checkAsync(CommandSender sender, String[] args) throws QueryException {
        guiAccessor = new MariaDBGuiAccessor();

        //Get the gui id and check that it exists
        guiId = guiAccessor.getGuiIdByName(args[2]);
        if (guiId == null) {
            returnGuiNotFoundError(sender, args[2]);
            return false;
        }

        //If sender is player, check that player is an editor of that gui
        if (sender instanceof Player p) {
            if (!p.hasPermission("traincartsticketshop.admin.gui.setdisplayname")) {
                if (!guiAccessor.playerCanEdit(guiId, p.getUniqueId().toString())) {
                    returnError(sender, "You do not have permission to edit that gui. Request that the owner adds you as an editor before making any changes");
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    protected void execute(CommandSender sender, String[] args) throws ModificationException {
        guiAccessor.updateGuiDisplayName(guiId, colouredDisplayName, rawDisplayName);
        sender.sendMessage(ChatColor.GREEN + "Gui \"" + args[2] + "\"'s display name was changed to \"" + colouredDisplayName + "\"");
    }
}
