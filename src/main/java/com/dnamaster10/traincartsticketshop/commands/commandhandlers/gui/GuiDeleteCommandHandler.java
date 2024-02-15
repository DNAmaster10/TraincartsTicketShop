package com.dnamaster10.traincartsticketshop.commands.commandhandlers.gui;

import com.dnamaster10.traincartsticketshop.commands.commandhandlers.AsyncCommandHandler;
import com.dnamaster10.traincartsticketshop.objects.guis.confirmguis.ConfirmGuiDeleteGui;
import com.dnamaster10.traincartsticketshop.util.Session;
import com.dnamaster10.traincartsticketshop.util.database.GuiAccessor;
import com.dnamaster10.traincartsticketshop.util.exceptions.DMLException;
import com.dnamaster10.traincartsticketshop.util.exceptions.DQLException;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static com.dnamaster10.traincartsticketshop.TraincartsTicketShop.getPlugin;

public class GuiDeleteCommandHandler extends AsyncCommandHandler {
    //Command example: /traincartsticketshop gui delete <gui_name>
    private GuiAccessor guiAccessor;
    private Integer guiId;
    @Override
    protected boolean checkSync(CommandSender sender, String[] args) {
        //Synchronous checks
        //Check config
        if (!getPlugin().getConfig().getBoolean("AllowGuiDelete")) {
            returnError(sender, "Gui deletion is disabled on this server");
            return false;
        }

        //Check permissions if player
        if (sender instanceof Player p) {
            if (!p.hasPermission("traincartsticketshop.gui.delete")) {
                returnInsufficientPermissionsError(sender);
                return false;
            }
        }

        //Check syntax
        if (args.length > 3) {
            returnInvalidSubCommandError(sender, args[3]);
            return false;
        }
        if (args.length < 3) {
            returnMissingArgumentsError(sender, "/tshop gui delete <gui name>");
            return false;
        }
        if (!checkGuiNameSyntax(args[2])) {
            returnGuiNotFoundError(sender, args[2]);
            return false;
        }

        return true;
    }

    @Override
    protected boolean checkAsync(CommandSender sender, String[] args) throws DQLException {
        guiAccessor = new GuiAccessor();

        //Get the gui id and check the gui exists
        guiId = guiAccessor.getGuiIdByName(args[2]);
        if (guiId == null) {
            returnGuiNotFoundError(sender, args[2]);
            return false;
        }

        //If sender is player, check they are owner
        if (sender instanceof Player p) {
            if (!guiAccessor.checkGuiOwnerByUuid(guiId, p.getUniqueId().toString())) {
                returnError(p, "You must be the owner of the gui in order to delete it");
                return false;
            }
        }
        return false;
    }

    @Override
    protected void execute(CommandSender sender, String[] args) throws DMLException {
        if (sender instanceof Player p) {
            ConfirmGuiDeleteGui newGui = new ConfirmGuiDeleteGui(guiId, p);
            Session session = getPlugin().getGuiManager().getNewSession(p);
            session.addGui(newGui);
            newGui.open();
            return;
        }
        //If sender isn't a player, we don't need to bother with a confirm action gui.
        //Delete the gui.
        guiAccessor.deleteGuiById(guiId);
        sender.sendMessage(ChatColor.GREEN + "Gui \"" + args[2] + "\" was deleted");
    }
}
