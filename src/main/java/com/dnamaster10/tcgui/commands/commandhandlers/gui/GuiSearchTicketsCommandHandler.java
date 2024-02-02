package com.dnamaster10.tcgui.commands.commandhandlers.gui;

import com.dnamaster10.tcgui.commands.commandhandlers.CommandHandler;
import com.dnamaster10.tcgui.objects.guis.TicketSearchGui;
import com.dnamaster10.tcgui.util.database.GuiAccessor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.StringJoiner;

public class GuiSearchTicketsCommandHandler extends CommandHandler<SQLException> {
    //Example command: /tcgui gui searchTickets <gui name> <search term>
    private String searchTerm;
    private GuiAccessor guiAccessor;
    @Override
    protected boolean checkSync(CommandSender sender, String[] args) {
        //Check config
        if (!getPlugin().getConfig().getBoolean("AllowGuiSearchTickets")) {
            returnError(sender, "Searching linkers is disabled on this server");
            return false;
        }
        //Check permission and that sender is player
        if (!(sender instanceof Player p)) {
            returnError(sender, "Command must be executed by a player");
            return false;
        }
        else {
            if (!p.hasPermission("tcgui.gui.search.searchtickets")) {
                returnError(sender, "You do not have permission to perform that action");
                return false;
            }
        }

        //Check syntax
        if (args.length < 4) {
            returnError(sender, "Missing argument(s): /tcgui gui searchTickets <gui name> <search term>");
            return false;
        }
        if (!checkGuiNameSyntax(args[2])) {
            returnGuiNotFoundError(sender, args[2]);
            return false;
        }

        StringJoiner joiner = new StringJoiner(" ");
        for (int i = 3; i < args.length; i++) {
            joiner.add(args[i]);
        }
        searchTerm = joiner.toString();
        if (searchTerm.length() > 25) {
            returnError(sender, "Search term cannot be longer than 25 characters in length");
            return false;
        }
        if (searchTerm.isBlank()) {
            returnError(sender, "Search term cannot be less than 1 character in length");
            return false;
        }
        return true;
    }

    @Override
    protected boolean checkAsync(CommandSender sender, String[] args) throws SQLException {
        //Check gui exists
        guiAccessor = new GuiAccessor();
        if (!guiAccessor.checkGuiByName(args[2])) {
            returnGuiNotFoundError(sender, args[2]);
            return false;
        }
        return true;
    }

    @Override
    protected void execute(CommandSender sender, String[] args) throws SQLException {
        //Create new gui
        TicketSearchGui gui = new TicketSearchGui(args[2], searchTerm, (Player) sender);

        //Open the gui
        gui.open();

        //Clear previous guis opened by the player
        getPlugin().getGuiManager().clearGuis((Player) sender);

        //Register the gui
        getPlugin().getGuiManager().addGui((Player) sender, gui);
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
                getPlugin().reportSqlError(sender, e);
            }
        });
    }
}
