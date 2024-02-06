package com.dnamaster10.tcgui.commands.commandhandlers.company;

import com.dnamaster10.tcgui.commands.commandhandlers.CommandHandler;
import com.dnamaster10.tcgui.util.Players;
import com.dnamaster10.tcgui.util.database.CompanyAccessor;
import com.dnamaster10.tcgui.util.database.databaseobjects.PlayerDatabaseObject;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class CompanyAddMemberCommandHandler extends CommandHandler {
    //Example Command: /tcgui company addMember <player name> <company name>
    CompanyAccessor companyAccessor;
    PlayerDatabaseObject newMember;
    Integer companyId;
    @Override
    protected boolean checkSync(CommandSender sender, String[] args) {
        //Check config
        if (!getPlugin().getConfig().getBoolean("AllowCompanyAddMember")) {
            returnError(sender, "Adding members to a company is disabled in the config");
            return false;
        }

        //Check permissions
        if (sender instanceof Player p) {
            if (!p.hasPermission("tcgui.company.addmember") && !p.hasPermission("tcgui.admin.company.addmember")) {
                returnError(sender, "You do not have permission to perform that action");
                return false;
            }
        }

        //Check syntax
        if (args.length < 4) {
            returnError(sender, "Missing argument(s): /tcgui company addMember <player name> <company name>");
            return false;
        }
        if (args.length > 4) {
            returnError(sender, "Invalid sub-command \"" + args[4] + "\"");
            return false;
        }
        if (!checkCompanyNameSyntax(args[3])) {
            returnCompanyNotFoundError(sender, args[3]);
            return false;
        }
        return true;
    }

    @Override
    protected boolean checkAsync(CommandSender sender, String[] args) throws SQLException {
        //Check that the company exists
        companyAccessor = new CompanyAccessor();
        if (!companyAccessor.checkCompanyByName(args[3])) {
            returnCompanyNotFoundError(sender, args[3]);
            return false;
        }

        //If player doesn't have admin permissions, check that they own the company
        if (sender instanceof Player p) {
            if (!p.hasPermission("tcgui.admin.company.addmember")) {
                if (!companyAccessor.checkIfOwner(args[3], p.getUniqueId().toString())) {
                    returnError(sender, "You do not own that company");
                    return false;
                }
            }
        }

        //Get and check the player
        newMember = Players.getPlayerByUsername(args[2]);
        if (newMember == null) {
            returnError(sender, "No player with the username \"" + args[2] + "\" could be found");
            return false;
        }

        //Get the company ID
        companyId = companyAccessor.getCompanyIdByName(args[3]);

        //Check the player isn't already a member
        if (companyAccessor.checkIfMember(companyId, newMember.getUuid())) {
            returnError(sender, "Player \"" + newMember.getUsername() + "\" is already a member of company \"" + args[3] + "\"");
            return false;
        }
        return true;
    }

    @Override
    protected void execute(CommandSender sender, String[] args) throws SQLException {
        //Add the member to the company
        companyAccessor.addMember(newMember, companyId);
        sender.sendMessage(ChatColor.GREEN + "Player \"" + newMember.getUsername() + "\" is now a member of company \"" + args[3] + "\"");
    }
}
