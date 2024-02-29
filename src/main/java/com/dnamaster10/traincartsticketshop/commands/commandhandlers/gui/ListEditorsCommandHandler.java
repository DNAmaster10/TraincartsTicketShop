package com.dnamaster10.traincartsticketshop.commands.commandhandlers.gui;

import com.dnamaster10.traincartsticketshop.commands.commandhandlers.AsyncCommandHandler;
import com.dnamaster10.traincartsticketshop.util.Utilities;
import com.dnamaster10.traincartsticketshop.util.database.mariadb.MariaDBGuiAccessor;
import com.dnamaster10.traincartsticketshop.util.database.mariadb.MariaDBGuiEditorsAccessor;
import com.dnamaster10.traincartsticketshop.util.exceptions.QueryException;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ListEditorsCommandHandler extends AsyncCommandHandler {
    //Example command: /tshop editor list <gui_name> <optional page number>

    private MariaDBGuiEditorsAccessor editorsAccessor;
    private Integer guiId;
    private Integer pageNumber;
    private Integer totalPages;

    private static final int playersPerPage = 5;
    @Override
    protected boolean checkSync(CommandSender sender, String[] args) {
        //Check permissions
        if (sender instanceof Player p) {
            if (!p.hasPermission("traincartsticketshop.gui.listeditors")) {
                returnInsufficientPermissionsError(sender);
                return false;
            }
        }

        //Check syntax
        if (args.length > 4) {
            returnInvalidSubCommandError(sender, args[5]);
            return false;
        }
        if (args.length < 3) {
            returnMissingArgumentsError(sender, "/tshop gui listEditors <gui_name> <optional page number>");
            return false;
        }

        if (args.length == 4) {
            if (!Utilities.isInt(args[3])) {
                returnError(sender, "Page number must be an integer");
                return false;
            }
            pageNumber = Integer.parseInt(args[3]);
            if (pageNumber < 1) {
                returnError(sender, "Page number cannot be less than 1");
                return false;
            }
        } else {
            pageNumber = 1;
        }

        if (!checkGuiNameSyntax(args[2])) {
            returnGuiNotFoundError(sender, args[2]);
            return false;
        }

        return true;
    }

    @Override
    protected boolean checkAsync(CommandSender sender, String[] args) throws QueryException {
        MariaDBGuiAccessor guiAccessor = new MariaDBGuiAccessor();

        //Get the guiID and check that it exists
        guiId = guiAccessor.getGuiIdByName(args[2]);
        if (guiId == null) {
            returnGuiNotFoundError(sender, args[2]);
            return false;
        }

        //Check permissions with owner access
        if (sender instanceof Player p) {
            if (!p.hasPermission("traincartsticketshop.admin.gui.listeditors")) {
                if (!guiAccessor.checkGuiOwnerByUuid(guiId, p.getUniqueId().toString())) {
                    returnError(sender, "You do not own that gui");
                    return false;
                }
            }
        }

        editorsAccessor = new MariaDBGuiEditorsAccessor();

        //Check there are any editors
        int totalEditors = editorsAccessor.getTotalEditors(guiId);
        if (totalEditors == 0) {
            returnError(sender, "There are no editors for gui \"" + args[2] + "\"");
            return false;
        }

        totalPages = Utilities.getPageCount(totalEditors, playersPerPage);
        //Check if the page is valid
        if (pageNumber > totalPages) {
            returnError(sender, "Page " + pageNumber + " does not exist");
            return false;
        }
        return true;
    }

    @Override
    protected void execute(CommandSender sender, String[] args) throws QueryException {
        String[] editors = editorsAccessor.getEditorUsernames(guiId, (pageNumber - 1) * playersPerPage, playersPerPage);

        //Build message
        List<TextComponent> lines = new ArrayList<>();
        lines.add(new TextComponent(ChatColor.AQUA + "Editors for gui \"" + args[2] + "\""));
        lines.get(0).setBold(true);

        for (String username : editors) {
            TextComponent message = new TextComponent(ChatColor.WHITE + " - " + username);
            lines.add(message);
        }

        TextComponent pageMessage = new TextComponent(ChatColor.AQUA + "");
        if (pageNumber > 1) {
            TextComponent prevPage = new TextComponent(ChatColor.AQUA + " <<< ");
            prevPage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tshop editor list " + args[2] + " " + (pageNumber - 1)));
            prevPage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.AQUA + "Prev Page")));
            pageMessage.addExtra(prevPage);
        }
        else {
            pageMessage.addExtra(ChatColor.AQUA + "     ");
        }

        pageMessage.addExtra(ChatColor.AQUA + "Page " + pageNumber + "/" + totalPages);

        if (pageNumber < totalPages) {
            TextComponent nextPage = new TextComponent(ChatColor.AQUA + " >>> ");
            nextPage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tshop editor list " + args[2] + " " + (pageNumber + 1)));
            nextPage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.AQUA + "Next Page")));
            pageMessage.addExtra(nextPage);
        }

        lines.add(pageMessage);

        //Send the message
        for (TextComponent component : lines) {
            sender.spigot().sendMessage(component);
        }
    }
}
