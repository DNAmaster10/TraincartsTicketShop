package com.dnamaster10.tcgui.commands.commandhandlers.linker;

import com.dnamaster10.tcgui.commands.commandhandlers.CommandHandler;
import com.dnamaster10.tcgui.objects.buttons.Linker;
import com.dnamaster10.tcgui.util.database.GuiAccessor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.StringJoiner;

public class LinkerCreateCommandHandler extends CommandHandler<SQLException> {
    //Example command: /tcgui linker create <linked_gui_name> <display_name>
    private String displayName;
    private GuiAccessor guiAccessor;
    @Override
    protected boolean checkSync(CommandSender sender, String[] args) {
        //Check config
        if (!getPlugin().getConfig().getBoolean("AllowLinkerCreate")) {
            returnError(sender, "Linker creation is disabled on this server");
            return false;
        }

        //Check sender is player and permissions
        if (!(sender instanceof Player p)) {
            returnError(sender, "Command must be executed by a player");
            return false;
        }
        else {
            if (!p.hasPermission("tcgui.linker.create")) {
                returnError(sender, "You do not have permission to perform that action");
                return false;
            }
        }
        //Check syntax
        if (args.length < 4) {
            returnError(sender, "Missing arguments: /tcgui linker create <linked_gui_name> <display_name>");
            return false;
        }
        if (!checkGuiNameSyntax(args[2])) {
            returnGuiNotFoundError(sender, args[2]);
            return false;
        }

        StringJoiner stringJoiner = new StringJoiner(" ");
        for (int i = 3; i < args.length; i++) {
            stringJoiner.add(args[i]);
        }
        displayName = stringJoiner.toString();
        if (displayName.length() > 25) {
            returnError(sender, "Linker display names cannot be more than 25 characters in length");
            return false;
        }
        if (displayName.isBlank()) {
            returnError(sender, "Linker display names cannot be less than 1 character in length");
            return false;
        }

        return true;
    }

    @Override
    protected boolean checkAsync(CommandSender sender, String[] args) throws SQLException {
        guiAccessor = new GuiAccessor();

        //Check that gui exists
        if (!guiAccessor.checkGuiByName(args[2])) {
            returnGuiNotFoundError(sender, args[2]);
            return false;
        }

        return true;
    }

    @Override
    protected void execute(CommandSender sender, String[] args) throws SQLException {
        //Get gui ID
        int guiId = guiAccessor.getGuiIdByName(args[2]);
        Linker button = new Linker(guiId, ChatColor.translateAlternateColorCodes('&', displayName));
        Player p = (Player) sender;
        button.giveToPlayer(p);
        p.sendMessage(ChatColor.GREEN + "Linker created");
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
