package com.dnamaster10.traincartsticketshop.commands.commandhandlers.linker;

import com.dnamaster10.traincartsticketshop.commands.commandhandlers.SyncCommandHandler;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.StringJoiner;

import static com.dnamaster10.traincartsticketshop.util.ButtonUtils.getButtonType;

public class LinkerSetDisplayNameCommandHandler extends SyncCommandHandler {
    //Example command: /tshop linker setDisplayName <display name>

    private String colouredDisplayName;
    private ItemStack linker;
    private Player player;
    @Override
    protected boolean checkSync(CommandSender sender, String[] args) {
        //Check that sender is a player
        if (!(sender instanceof Player)) {
            returnOnlyPlayersExecuteError(sender);
            return false;
        }
        player = (Player) sender;

        if (!player.hasPermission("traincartsticketshop.linker.rename")) {
            returnInsufficientPermissionsError(player);
            return false;
        }

        //Check syntax
        if (args.length < 3) {
            returnMissingArgumentsError(player, "/tshop linker setDisplayName <display name>");
            return false;
        }

        //Build display name
        StringJoiner stringJoiner = new StringJoiner(" ");
        for (int i = 2; i < args.length; i++) {
            stringJoiner.add(args[i]);
        }
        colouredDisplayName = ChatColor.translateAlternateColorCodes('&', stringJoiner.toString());
        String rawDisplayName = ChatColor.stripColor(colouredDisplayName);

        if (rawDisplayName.length() > 25) {
            returnError(player, "Linker names cannot be more than 25 characters in length");
            return false;
        }
        if (rawDisplayName.isBlank()) {
            returnError(player, "Linker names cannot be less than 1 character in length");
            return false;
        }
        if (colouredDisplayName.length() > 100) {
            returnError(player, "Too many colours!");
            return false;
        }

        //Check that player is holding a linker
        linker = player.getInventory().getItemInMainHand();
        String buttonType = getButtonType(linker);
        if (buttonType == null || !buttonType.equals("linker")) {
            returnWrongItemError(player, "linker");
            return false;
        }
        return true;
    }
    @Override
    protected void execute(CommandSender sender, String[] args) {
        //Get the item meta
        ItemMeta meta = linker.getItemMeta();
        assert meta != null;

        //Set the data
        meta.setDisplayName(colouredDisplayName);
        linker.setItemMeta(meta);
        player.sendMessage(ChatColor.GREEN + "Held linker was renamed to \"" + colouredDisplayName + "\"");
    }
}
