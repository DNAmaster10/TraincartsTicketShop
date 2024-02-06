package com.dnamaster10.tcgui.commands.commandhandlers.gui;

import com.dnamaster10.tcgui.commands.commandhandlers.CommandHandler;
import com.dnamaster10.tcgui.util.database.GuiAccessor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.StringJoiner;

public class GuiCreateCommandHandler extends CommandHandler {
    //Example command: /tcgui gui create <gui name> <display name>

    //Used to store the display name since spaces can be entered here
    private String rawDisplayName;
    private String colouredDisplayName;
    private GuiAccessor guiAccessor;
    @Override
    protected boolean checkSync(CommandSender sender, String[] args) {
        //Synchronous checks (Syntax etc.)
        //Check config
        if (!getPlugin().getConfig().getBoolean("AllowGuiCreate")) {
            returnError(sender, "Gui creation is disabled on this server");
            return false;
        }

        //Check sender is player and permissions
        if (!(sender instanceof Player)) {
            returnError(sender, "Command must be executed by a player");
            return false;
        }
        if (!sender.hasPermission("tcgui.gui.create")) {
            returnError(sender, "You do not have permission to perform that action");
            return false;
        }


        //Check syntax
        if (args.length < 4) {
            returnError(sender, "Invalid syntax: /tcgui gui create <gui name> <display name>");
            return false;
        }
        if (args[2].length() > 20) {
            returnError(sender, "Gui names cannot be more than 20 characters in length");
            return false;
        }
        if (args[2].length() < 3) {
            returnError(sender, "Gui names cannot be less than 3 characters in length");
            return false;
        }
        if (!checkStringFormat(args[2])) {
            returnError(sender, "Gui names can only contain characters Aa to Zz, numbers, underscores and dashes");
            return false;
        }

        //Build display name
        StringJoiner stringJoiner = new StringJoiner(" ");
        for (int i = 3; i < args.length; i++) {
            stringJoiner.add(args[i]);
        }
        colouredDisplayName = ChatColor.translateAlternateColorCodes('&', stringJoiner.toString());
        rawDisplayName = ChatColor.stripColor(colouredDisplayName);

        //Check display name
        if (rawDisplayName.length() > 25) {
            returnError(sender, "Gui display names cannot be longer than 25 characters in length");
            return false;
        }
        if (rawDisplayName.isBlank()) {
            returnError(sender, "Gui display names cannot be less than 1 character in length");
            return false;
        }

        //If all checks have passed return true
        return true;
    }

    @Override
    protected boolean checkAsync(CommandSender sender, String[] args) throws SQLException {
        //Asynchronous checks (Database etc.)
        //Method must be run from an already asynchronous method in order to be async
        Player p = (Player) sender;
        String guiName = args[2];
        guiAccessor = new GuiAccessor();

        //Check gui doesn't already exist
        if (guiAccessor.checkGuiByName(guiName)) {
            returnError(p, "A gui with the name \"" + guiName + "\" already exists");
            return false;
        }
        return true;
    }

    @Override
    protected void execute(CommandSender sender, String[] args) throws SQLException {
        //Runs the command
        guiAccessor.addGui(args[2], colouredDisplayName, rawDisplayName, ((Player) sender).getUniqueId().toString());
        sender.sendMessage(ChatColor.GREEN + "A gui with name \"" + args[2] + "\" was created");
    }
}
