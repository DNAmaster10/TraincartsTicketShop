package com.dnamaster10.traincartsticketshop.commands.commandhandlers.linker;

import com.dnamaster10.traincartsticketshop.commands.commandhandlers.AsyncCommandHandler;
import com.dnamaster10.traincartsticketshop.objects.buttons.Linker;
import com.dnamaster10.traincartsticketshop.util.database.GuiAccessor;
import com.dnamaster10.traincartsticketshop.util.exceptions.DQLException;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.StringJoiner;

public class LinkerCreateCommandHandler extends AsyncCommandHandler {
    //Example command: /tshop linker create <linked_gui_name> <display_name>
    private String colouredDisplayName;
    private Player player;
    private Integer guiId;
    @Override
    protected boolean checkSync(CommandSender sender, String[] args) {
        //Check sender is player and permissions
        if (!(sender instanceof Player)) {
            returnOnlyPlayersExecuteError(sender);
            return false;
        }
        player = (Player) sender;

        if (!player.hasPermission("traincartsticketshop.linker.create")) {
            returnInsufficientPermissionsError(player);
            return false;
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
        colouredDisplayName = ChatColor.translateAlternateColorCodes('&', stringJoiner.toString());
        String rawDisplayName = ChatColor.stripColor(colouredDisplayName);

        if (rawDisplayName.length() > 25) {
            returnError(player, "Linker display names cannot be more than 25 characters in length");
            return false;
        }
        if (rawDisplayName.isBlank()) {
            returnError(player, "Linker display names cannot be less than 1 character in length");
            return false;
        }
        if (colouredDisplayName.length() > 100) {
            returnError(player, "Too many colours used in display name");
            return false;
        }

        return true;
    }

    @Override
    protected boolean checkAsync(CommandSender sender, String[] args) throws DQLException {
        GuiAccessor guiAccessor = new GuiAccessor();

        //Check that the gui exists
        guiId = guiAccessor.getGuiIdByName(args[2]);
        if (guiId == null) {
            returnGuiNotFoundError(player, args[2]);
            return false;
        }

        return true;
    }

    @Override
    protected void execute(CommandSender sender, String[] args) throws DQLException {
        //Create the linker
        Linker linker = new Linker(guiId, 0, colouredDisplayName);

        //Give the linker to the player
        ItemStack item = linker.getItemStack();
        player.getInventory().addItem(item);

        player.sendMessage(ChatColor.GREEN + "Linker created");
    }
}
