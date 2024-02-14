package com.dnamaster10.traincartsticketshop.commands.commandhandlers.linker;

import com.dnamaster10.traincartsticketshop.commands.commandhandlers.AsyncCommandHandler;
import com.dnamaster10.traincartsticketshop.commands.commandhandlers.SyncCommandHandler;
import com.dnamaster10.traincartsticketshop.objects.buttons.Linker;
import com.dnamaster10.traincartsticketshop.util.database.GuiAccessor;
import com.dnamaster10.traincartsticketshop.util.exceptions.DMLException;
import com.dnamaster10.traincartsticketshop.util.exceptions.DQLException;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.StringJoiner;

public class LinkerCreateCommandHandler extends AsyncCommandHandler {
    //Example command: /traincartsticketshop linker create <linked_gui_name> <display_name>
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
            returnOnlyPlayersExecuteError(sender);
            return false;
        }
        else {
            player = p;
            if (!player.hasPermission("traincartsticketshop.linker.create")) {
                returnInsufficientPermissionsError(player);
                return false;
            }
        }
        //Check syntax
        if (args.length < 4) {
            returnMissingArgumentsError(player, "/tshop linker create <linked_gui_name> <display_name>");
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
    protected boolean checkAsync(CommandSender sender, String[] args) throws DQLException {
        guiAccessor = new GuiAccessor();

        //Check that gui exists
        if (!guiAccessor.checkGuiByName(args[2])) {
            returnGuiNotFoundError(player, args[2]);
            return false;
        }

        return true;
    }

    @Override
    protected void execute(CommandSender sender, String[] args) throws DQLException {
        //Get gui ID
        int guiId = guiAccessor.getGuiIdByName(args[2]);

        //Create the linker
        Linker linker = new Linker(guiId, 0, displayName);

        //Give the linker to the player
        ItemStack item = linker.getItemStack();
        player.getInventory().addItem(item);

        player.sendMessage(ChatColor.GREEN + "Linker created");
    }
}
