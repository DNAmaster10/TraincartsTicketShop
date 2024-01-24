package com.dnamaster10.tcgui.commands.commandhandlers.linker;

import com.dnamaster10.tcgui.commands.commandhandlers.CommandHandler;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.sql.SQLException;

public class LinkerRenameCommandHandler extends CommandHandler<SQLException> {
    //tcgui linker rename <display_name>
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

        //Check syntax
        if (args.length < 3) {
            returnError(sender, "Please enter a new name for the linker");
            return false;
        }
        if (args.length > 3) {
            returnError(sender, "Invalid sub-command \"" + args[3] + "\"");
            return false;
        }
        if (args[2].length() > 20) {
            returnError(sender, "Linker display names cannot be more than 20 characters in length");
            return false;
        }
        if (args[2].isEmpty()) {
            returnError(sender, "Linker display names cannot be less than 1 character in length");
            return false;
        }
        if (!checkStringFormat(args[2])) {
            returnError(sender, "Linker display names can only contain characters Aa - Zz, numbers, underscores and dashes");
            return false;
        }

        //Check permissions
        if (!sender.hasPermission("tcgui.linker.rename")) {
            returnError(sender, "You do not have permission to perform that action");
            return false;
        }

        //Check that player is holding a linker
        ItemStack linker = ((Player) sender).getInventory().getItemInMainHand();
        if (!linker.hasItemMeta()) {
            returnError(sender, "You must be holding a linker item in your main hand");
            return false;
        }
        ItemMeta meta = linker.getItemMeta();
        NamespacedKey key = new NamespacedKey(getPlugin(), "button_type");
        assert meta != null;
        if (!meta.getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
            returnError(sender, "You must be holding a linker item in your main hand");
            return false;
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
        meta.setDisplayName(args[2]);
        linker.setItemMeta(meta);
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        if (!checkSync(sender, args)) {
            return;
        }
        execute(sender, args);
    }
}
