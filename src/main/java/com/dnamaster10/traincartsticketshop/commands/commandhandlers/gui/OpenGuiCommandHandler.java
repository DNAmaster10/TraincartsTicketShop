package com.dnamaster10.traincartsticketshop.commands.commandhandlers.gui;

import com.dnamaster10.traincartsticketshop.commands.commandhandlers.AsyncCommandHandler;
import com.dnamaster10.traincartsticketshop.objects.guis.multipageguis.ShopGui;
import com.dnamaster10.traincartsticketshop.util.Session;
import com.dnamaster10.traincartsticketshop.util.database.AccessorFactory;
import com.dnamaster10.traincartsticketshop.util.database.accessorinterfaces.GuiAccessor;
import com.dnamaster10.traincartsticketshop.util.exceptions.QueryException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static com.dnamaster10.traincartsticketshop.TraincartsTicketShop.getPlugin;

public class OpenGuiCommandHandler extends AsyncCommandHandler {
    private Player player;
    private GuiAccessor guiAccessor;

    @Override
    protected boolean checkSync(CommandSender sender, String[] args) {
        //Check sender is player
        if (!(sender instanceof Player)) {
            returnOnlyPlayersExecuteError(sender);
            return false;
        }
        player = (Player) sender;

        //Check permissions
        if (!player.hasPermission("traincartsticketshop.gui.open")) {
            returnInsufficientPermissionsError(sender);
            return false;
        }

        //Check syntax
        if (args.length < 3) {
            returnMissingArgumentsError(sender, "/tshop gui open <gui name>");
            return false;
        }
        if (args.length > 3) {
            returnInvalidSubCommandError(sender, args[3]);
            return false;
        }
        if (!checkGuiNameSyntax(args[2])) {
            returnGuiNotFoundError(sender, args[2]);
            return false;
        }

        return true;
    }

    @Override
    protected boolean checkAsync(CommandSender sender, String[] args) throws QueryException {
        //Example command: /traincartsticketshop gui open <gui_name>
        guiAccessor = AccessorFactory.getGuiAccessor();

        //Get the guiID and check the gui exists
        if (!guiAccessor.checkGuiByName(args[2])) {
            returnGuiNotFoundError(player, args[2]);
            return false;
        }
        return true;
    }

    @Override
    protected void execute(CommandSender sender, String[] args) throws QueryException {
        //Create a new gui
        int guiId = guiAccessor.getGuiIdByName(args[2]);
        ShopGui gui = new ShopGui(guiId, player);

        //Create a new gui session
        Session session = getPlugin().getGuiManager().getNewSession(player);

        //Register the new gui
        session.addGui(gui);

        //Open the new gui
        gui.open();
    }
}
