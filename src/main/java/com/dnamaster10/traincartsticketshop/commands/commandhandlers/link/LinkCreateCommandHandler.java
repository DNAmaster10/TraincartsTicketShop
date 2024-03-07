package com.dnamaster10.traincartsticketshop.commands.commandhandlers.link;

import com.dnamaster10.traincartsticketshop.commands.commandhandlers.AsyncCommandHandler;
import com.dnamaster10.traincartsticketshop.objects.buttons.Link;
import com.dnamaster10.traincartsticketshop.util.database.AccessorFactory;
import com.dnamaster10.traincartsticketshop.util.database.accessorinterfaces.GuiAccessor;
import com.dnamaster10.traincartsticketshop.util.exceptions.QueryException;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.StringJoiner;

public class LinkCreateCommandHandler extends AsyncCommandHandler {
    //Example command: /tshop link create <linked gui name> <optional display name>
    private String colouredDisplayName;
    private Player player;
    private GuiAccessor guiAccessor;
    @Override
    protected boolean checkSync(CommandSender sender, String[] args) {
        //Check sender is player and permissions
        if (!(sender instanceof Player)) {
            returnOnlyPlayersExecuteError(sender);
            return false;
        }
        player = (Player) sender;

        if (!player.hasPermission("traincartsticketshop.link.create")) {
            returnInsufficientPermissionsError(player);
            return false;
        }
        //Check syntax
        if (args.length < 3) {
            returnMissingArgumentsError(player, "/tshop link create <linked gui name> <optional display name>");
            return false;
        }
        if (!checkGuiNameSyntax(args[2])) {
            returnGuiNotFoundError(player, args[2]);
            return false;
        }

        //Build display name
        String rawDisplayName = null;
        if (args.length > 3) {
            StringJoiner stringJoiner = new StringJoiner(" ");
            for (int i = 3; i < args.length; i++) {
                stringJoiner.add(args[i]);
            }
            colouredDisplayName = ChatColor.translateAlternateColorCodes('&', stringJoiner.toString());
            rawDisplayName = ChatColor.stripColor(colouredDisplayName);
        } else {
            colouredDisplayName = args[2];
            rawDisplayName = args[2];
        }
        if (rawDisplayName.length() > 25) {
            returnError(player, "Link display names cannot be more than 25 characters in length");
            return false;
        }
        if (rawDisplayName.isBlank()) {
            returnError(player, "Link display names cannot be less than 1 character in length");
            return false;
        }
        if (colouredDisplayName.length() > 100) {
            returnError(player, "Too many colours used in display name");
            return false;
        }

        return true;
    }

    @Override
    protected boolean checkAsync(CommandSender sender, String[] args) throws QueryException {
        guiAccessor = AccessorFactory.getGuiAccessor();

        //Check that the gui exists
        if (!guiAccessor.checkGuiByName(args[2])) {
            returnGuiNotFoundError(sender, args[2]);
            return false;
        }

        return true;
    }

    @Override
    protected void execute(CommandSender sender, String[] args) throws QueryException {
        //Get gui ID
        int guiId = guiAccessor.getGuiIdByName(args[2]);

        //Create the link
        Link link = new Link(guiId, 0, colouredDisplayName);

        //Give the link to the player
        ItemStack item = link.getItemStack();
        player.getInventory().addItem(item);

        player.sendMessage(ChatColor.GREEN + "Link created");
    }
}
