package com.dnamaster10.tcgui.commands.commandhandlers.linker;

import com.dnamaster10.tcgui.commands.commandhandlers.ItemCommandHandler;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.SQLException;
import java.util.Objects;
import java.util.StringJoiner;

public class LinkerSetDisplayNameCommandHandler extends ItemCommandHandler {
    private String colouredDisplayName;
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
            if (!p.hasPermission("tcgui.linker.rename")) {
                returnError(sender, "You do not have permission to perform that action");
                return false;
            }
        }

        //Check syntax
        if (args.length < 3) {
            returnError(sender, "Please enter a new name for the linker");
            return false;
        }

        //Build display name
        StringJoiner stringJoiner = new StringJoiner(" ");
        for (int i = 2; i < args.length; i++) {
            stringJoiner.add(args[i]);
        }
        colouredDisplayName = ChatColor.translateAlternateColorCodes('&', stringJoiner.toString());
        //tcgui linker rename <display_name>
        String rawDisplayName = ChatColor.stripColor(colouredDisplayName);

        if (rawDisplayName.length() > 25) {
            returnError(sender, "Linker names cannot be more than 25 characters in length");
            return false;
        }
        if (rawDisplayName.isBlank()) {
            returnError(sender, "Linker names cannot be less than 1 character in length");
            return false;
        }
        if (colouredDisplayName.length() > 100) {
            returnError(sender, "Too many colours!");
            return false;
        }

        //Check that player is holding a linker
        ItemStack linker = ((Player) sender).getInventory().getItemInMainHand();
        String buttonType = getButtonType(linker);
        if (!Objects.equals(buttonType, "linker")) {
            returnWrongItemError(sender, "linker");
        }
        return true;
    }

    @Override
    protected boolean checkAsync(CommandSender sender, String[] args) throws SQLException {
        return true;
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        //Get item
        ItemStack linker = ((Player) sender).getInventory().getItemInMainHand();
        ItemMeta meta = linker.getItemMeta();
        assert meta != null;
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
