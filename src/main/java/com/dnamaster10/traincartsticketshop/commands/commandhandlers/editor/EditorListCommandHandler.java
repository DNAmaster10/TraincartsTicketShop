package com.dnamaster10.traincartsticketshop.commands.commandhandlers.editor;

import com.dnamaster10.traincartsticketshop.commands.commandhandlers.AsyncCommandHandler;
import com.dnamaster10.traincartsticketshop.util.database.GuiAccessor;
import com.dnamaster10.traincartsticketshop.util.exceptions.DQLException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static com.dnamaster10.traincartsticketshop.TraincartsTicketShop.getPlugin;

public class EditorListCommandHandler extends AsyncCommandHandler {
    //TODO command needs finishing and redoing in places
    //Example command: /traincartsticketshop gui editor list <gui_name> <optional page number>
    private GuiAccessor guiAccessor;
    private Integer guiId;
    @Override
    protected boolean checkSync(CommandSender sender, String[] args) {
        //Check permissions
        if (sender instanceof Player p) {
            if (!p.hasPermission("traincartsticketshop.editor.list")) {
                returnInsufficientPermissionsError(sender);
                return false;
            }
        }

        //Check syntax
        if (args.length > 5) {
            returnInvalidSubCommandError(sender, args[4]);
            return false;
        }
        if (args.length < 4) {
            returnMissingArgumentsError(sender, "/tshop gui editor list <gui_name> <optional page number>");
            return false;
        }
        if (!checkGuiNameSyntax(args[3])) {
            returnGuiNotFoundError(sender, args[3]);
            return false;
        }

        return true;
    }

    @Override
    protected boolean checkAsync(CommandSender sender, String[] args) throws DQLException {
        guiAccessor = new GuiAccessor();

        //Get the guiID and check that it exists
        guiId = guiAccessor.getGuiIdByName(args[3]);
        if (guiId == null) {
            returnGuiNotFoundError(sender, args[3]);
            return false;
        }

        //If sender is a player
        if (sender instanceof Player p) {
            //If players aren't able to view editor for other people's guis
            if (!getPlugin().getConfig().getBoolean("AllowListGuiEditorsOtherOwners")) {
                //If player isn't the owner or editor
                if (guiAccessor.playerCanEdit(guiId, p.getUniqueId().toString())) {
                    returnError(sender, "You do not have permission to view the editors for that gui");
                    return false;
                }
            }
        }
        //Get players UUIDs
        List<String> uuids = new ArrayList<>();

        return true;
    }

    @Override
    protected void execute(CommandSender sender, String[] args) throws DQLException {

    }
}
