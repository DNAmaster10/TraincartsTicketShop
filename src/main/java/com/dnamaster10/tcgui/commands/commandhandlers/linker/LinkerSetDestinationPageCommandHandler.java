package com.dnamaster10.tcgui.commands.commandhandlers.linker;

import com.dnamaster10.tcgui.commands.commandhandlers.CommandHandler;
import com.dnamaster10.tcgui.commands.commandhandlers.ItemCommandHandler;
import com.dnamaster10.tcgui.util.Utilities;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.sql.SQLException;
import java.util.Objects;

import static com.dnamaster10.tcgui.objects.buttons.DataKeys.BUTTON_TYPE;
import static com.dnamaster10.tcgui.objects.buttons.DataKeys.DEST_GUI_PAGE;

public class LinkerSetDestinationPageCommandHandler extends ItemCommandHandler {
    //Example command: /tcgui linker setDestinationPage <destination page>
    @Override
    protected boolean checkSync(CommandSender sender, String[] args) {
        //Check config
        if (!getPlugin().getConfig().getBoolean("AllowLinkerSetDestinationPage")) {
            returnError(sender, "Setting the destination page for a linker is disabled on this server");
            return false;
        }

        //Check perms and that sender is a player
        if (!(sender instanceof Player p)) {
            returnError(sender, "Command must be executed by a player");
            return false;
        }
        else {
            if (!p.hasPermission("tcgui.linker.setdestinationpage")) {
                returnError(sender, "You do not have permission to perform that action");
                return false;
            }
        }

        //Check syntax
        if (args.length < 3) {
            returnError(sender, "Missing argument(s): /tcgui linker setDestinationPage <destination page>");
            return false;
        }
        if (args.length > 3) {
            returnError(sender, "Invalid sub-command \"" + args[3] + "\"");
            return false;
        }
        if (!Utilities.isInt(args[2])) {
            returnError(sender, "Page number must be a valid integer");
            return false;
        }

        //Check that player is holding a linker
        ItemStack linker = ((Player) sender).getInventory().getItemInMainHand();
        String buttonType = getButtonType(linker);
        if (!Objects.equals(buttonType, "linker")) {
            returnWrongItemError(sender, "linker");
            return false;
        }
        return true;
    }

    @Override
    protected boolean checkAsync(CommandSender sender, String[] args) throws SQLException {
        return false;
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        ItemStack linker = ((Player) sender).getInventory().getItemInMainHand();
        ItemMeta meta = linker.getItemMeta();
        assert meta != null;
        PersistentDataContainer dataContainer = meta.getPersistentDataContainer();

        dataContainer.set(DEST_GUI_PAGE, PersistentDataType.INTEGER, Integer.parseInt(args[2]) - 1);
        linker.setItemMeta(meta);

        sender.sendMessage(ChatColor.GREEN + "Linker page set");
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        if (!checkSync(sender, args)) {
            return;
        }
        execute(sender, args);
    }
}
