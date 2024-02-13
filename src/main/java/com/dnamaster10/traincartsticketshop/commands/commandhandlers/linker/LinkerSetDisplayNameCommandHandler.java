package com.dnamaster10.traincartsticketshop.commands.commandhandlers.linker;

import com.dnamaster10.traincartsticketshop.commands.commandhandlers.ItemCommandHandler;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.SQLException;
import java.util.Objects;
import java.util.StringJoiner;

import static com.dnamaster10.traincartsticketshop.objects.buttons.Buttons.getButtonType;

public class LinkerSetDisplayNameCommandHandler extends ItemCommandHandler {
    private String colouredDisplayName;
    private ItemStack linker;
    private Player player;
    @Override
    protected boolean checkSync(CommandSender sender, String[] args) {
        //Check config
        if (!getPlugin().getConfig().getBoolean("AllowLinkerRename")) {
            returnError(sender, "Linker renaming is disabled on this server");
            return false;
        }

        //Check that sender is a player
        if (!(sender instanceof Player p)) {
            returnError(sender, "Command must be executed by a player ");
            return false;
        }
        else {
            player = p;
            if (!player.hasPermission("traincartsticketshop.linker.rename")) {
                returnError(player, "You do not have permission to perform that action");
                return false;
            }
        }

        //Check syntax
        if (args.length < 3) {
            returnError(player, "Please enter a new name for the linker");
            return false;
        }

        //Build display name
        StringJoiner stringJoiner = new StringJoiner(" ");
        for (int i = 2; i < args.length; i++) {
            stringJoiner.add(args[i]);
        }
        colouredDisplayName = ChatColor.translateAlternateColorCodes('&', stringJoiner.toString());
        //traincartsticketshop linker rename <display_name>
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
        }
        return true;
    }

    @Override
    protected boolean checkAsync(CommandSender sender, String[] args) throws SQLException {
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
        sender.sendMessage(ChatColor.GREEN + "Held linker was renamed to \"" + colouredDisplayName + "\"");
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        if (!checkSync(sender, args)) {
            return;
        }
        execute(sender, args);
    }
}
