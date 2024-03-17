package com.dnamaster10.traincartsticketshop.commands.commandhandlers.gui;

import com.dnamaster10.traincartsticketshop.commands.commandhandlers.AsyncCommandHandler;
import com.dnamaster10.traincartsticketshop.util.database.AccessorFactory;
import com.dnamaster10.traincartsticketshop.util.database.accessorinterfaces.GuiAccessor;
import com.dnamaster10.traincartsticketshop.util.exceptions.ModificationException;
import com.dnamaster10.traincartsticketshop.util.exceptions.QueryException;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.StringJoiner;

public class RenameGuiCommandHandler extends AsyncCommandHandler {
    //Example command: /traincartsticketshop gui setdisplayname <gui name> <gui display name>
    private String rawDisplayName;
    private String colouredDisplayName;
    private GuiAccessor guiAccessor;
    private Integer guiId;
    @Override
    protected boolean checkSync(CommandSender sender, String[] args) {
        //Check permissions
        if (sender instanceof Player p) {
            if (!p.hasPermission("traincartsticketshop.gui.rename") && !p.hasPermission("traincartsticketshop.admin.gui.rename")) {
                returnInsufficientPermissionsError(sender);
                return false;
            }
        }

        //Check syntax
        if (args.length < 4) {
            returnMissingArgumentsError(sender, "/tshop rename <gui id> <new name>");
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
            returnError(sender, "Gui names cannot be more than 20 characters in length");
            return false;
        }
        if (rawDisplayName.isBlank()) {
            returnError(sender, "Gui names cannot be less than 1 character in length");
            return false;
        }
        if (colouredDisplayName.length() > 100) {
            returnError(sender, "Too many colours used in name");
            return false;
        }
        return true;
    }

    @Override
    protected boolean checkAsync(CommandSender sender, String[] args) throws QueryException {
        guiAccessor = AccessorFactory.getGuiAccessor();

        //Get the gui id and check that it exists
        if (!guiAccessor.checkGuiByName(args[2])) {
            returnGuiNotFoundError(sender, args[2]);
            return false;
        }
        guiId = guiAccessor.getGuiIdByName(args[2]);

        //If sender is player, check that player is an editor of that gui
        if (sender instanceof Player p) {
            if (!p.hasPermission("traincartsticketshop.admin.gui.rename")) {
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
        sender.sendMessage(ChatColor.GREEN + "Gui \"" + args[2] + "\"'s name was changed to \"" + colouredDisplayName + "\"");
    }
}
