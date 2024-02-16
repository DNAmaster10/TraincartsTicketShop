package com.dnamaster10.traincartsticketshop.commands.commandhandlers.linker;

import com.dnamaster10.traincartsticketshop.commands.commandhandlers.SyncCommandHandler;
import com.dnamaster10.traincartsticketshop.util.Utilities;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.sql.SQLException;

import static com.dnamaster10.traincartsticketshop.TraincartsTicketShop.getPlugin;
import static com.dnamaster10.traincartsticketshop.objects.buttons.Buttons.getButtonType;
import static com.dnamaster10.traincartsticketshop.objects.buttons.DataKeys.DEST_GUI_PAGE;

public class LinkerSetDestinationPageCommandHandler extends SyncCommandHandler {
    //Example command: /traincartsticketshop linker setDestinationPage <destination page>
    private Player player;
    private ItemStack linker;
    @Override
    protected boolean checkSync(CommandSender sender, String[] args) {
        //Check config
        if (!getPlugin().getConfig().getBoolean("AllowLinkerSetDestinationPage")) {
            returnError(sender, "Setting the destination page for a linker is disabled on this server");
            return false;
        }

        //Check perms and that sender is a player
        if (!(sender instanceof Player)) {
            returnOnlyPlayersExecuteError(sender);
            return false;
        }
        player = (Player) sender;

        if (!player.hasPermission("traincartsticketshop.linker.setdestinationpage")) {
            returnInsufficientPermissionsError(player);
            return false;
        }

        //Check syntax
        if (args.length < 3) {
            returnMissingArgumentsError(player, "/tshop linker setDestinationPage <destination page>");
            return false;
        }
        if (args.length > 3) {
            returnInvalidSubCommandError(player, args[3]);
            return false;
        }
        if (!Utilities.isInt(args[2])) {
            returnError(player, "Page number must be a valid integer");
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
        //Get the linker meta
        ItemMeta meta = linker.getItemMeta();
        assert meta != null;

        //Add the keys and data
        PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
        dataContainer.set(DEST_GUI_PAGE, PersistentDataType.INTEGER, Integer.parseInt(args[2]) - 1);
        linker.setItemMeta(meta);

        player.sendMessage(ChatColor.GREEN + "Linker page set");
    }
}
