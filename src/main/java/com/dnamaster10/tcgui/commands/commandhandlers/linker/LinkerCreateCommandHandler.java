package com.dnamaster10.tcgui.commands.commandhandlers.linker;

import com.dnamaster10.tcgui.commands.commandhandlers.CommandHandler;
import com.dnamaster10.tcgui.commands.commandhandlers.ItemCommandHandler;
import com.dnamaster10.tcgui.objects.buttons.Linker;
import com.dnamaster10.tcgui.util.database.GuiAccessor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.StringJoiner;

public class LinkerCreateCommandHandler extends ItemCommandHandler {
    //Example command: /tcgui linker create <linked_gui_name> <display_name>
    private String displayName;
    private GuiAccessor guiAccessor;
    private Player player;
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
            player = p;
            if (!player.hasPermission("tcgui.linker.create")) {
                returnError(player, "You do not have permission to perform that action");
                return false;
            }
        }
        //Check syntax
        if (args.length < 4) {
            returnError(player, "Missing arguments: /tcgui linker create <linked_gui_name> <display_name>");
            return false;
        }
        if (!checkGuiNameSyntax(args[2])) {
            returnGuiNotFoundError(player, args[2]);
            return false;
        }

        StringJoiner stringJoiner = new StringJoiner(" ");
        for (int i = 3; i < args.length; i++) {
            stringJoiner.add(args[i]);
        }
        displayName = stringJoiner.toString();
        if (displayName.length() > 25) {
            returnError(player, "Linker display names cannot be more than 25 characters in length");
            return false;
        }
        if (displayName.isBlank()) {
            returnError(player, "Linker display names cannot be less than 1 character in length");
            return false;
        }

        return true;
    }

    @Override
    protected boolean checkAsync(CommandSender sender, String[] args) throws SQLException {
        guiAccessor = new GuiAccessor();

        //Check that gui exists
        if (!guiAccessor.checkGuiByName(args[2])) {
            returnGuiNotFoundError(player, args[2]);
            return false;
        }

        return true;
    }

    @Override
    protected void execute(CommandSender sender, String[] args) throws SQLException {
        //Get gui ID
        int guiId = guiAccessor.getGuiIdByName(args[2]);

        //Create the linker
        //TODO This probably needs looking at a bit more
        Linker linker = new Linker(guiId, 0, ChatColor.translateAlternateColorCodes('&', displayName));

        //Give the linker to the player
        ItemStack item = linker.getItemStack();
        player.getInventory().addItem(item);

        player.sendMessage(ChatColor.GREEN + "Linker created");
    }
}
