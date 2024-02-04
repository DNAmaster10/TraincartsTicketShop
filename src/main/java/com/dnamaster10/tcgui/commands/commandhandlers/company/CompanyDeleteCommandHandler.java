package com.dnamaster10.tcgui.commands.commandhandlers.company;

import com.dnamaster10.tcgui.commands.commandhandlers.CommandHandler;
import com.dnamaster10.tcgui.util.database.CompanyAccessor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class CompanyDeleteCommandHandler extends CommandHandler<SQLException> {
    //Example command: /tcgui company delete <company name>
    private CompanyAccessor companyAccessor;
    @Override
    protected boolean checkSync(CommandSender sender, String[] args) {
        //Check config
        if (!getPlugin().getConfig().getBoolean("AllowCompanyDelete")) {
            returnError(sender, "Deleting companies is disabled on this server");
            return false;
        }

        //Check permissions
        if (sender instanceof Player p) {
            if (!p.hasPermission("tcgui.company.delete") && !p.hasPermission("tcgui.admin.company.delete")) {
                returnError(sender, "You do not have permission to perform that action");
                return false;
            }
        }

        //Check syntax
        if (args.length > 3) {
            returnError(sender, "Invalid sub-command \"" + args[3] + "\"");
            return false;
        }
        if (args.length < 3) {
            returnError(sender, "Missing argument(s): /tcgui company delete <company name>");
            return false;
        }
        if (!checkCompanyNameSyntax(args[2])) {
            returnCompanyNotFoundError(sender, args[2]);
            return false;
        }

        return true;
    }
    @Override
    protected boolean checkAsync(CommandSender sender, String[] args) throws SQLException {
        companyAccessor = new CompanyAccessor();

        //Check that company exists
        if (!companyAccessor.checkCompanyByName(args[2])) {
            returnCompanyNotFoundError(sender, args[2]);
            return false;
        }

        //Check that player owns the company or skip if they have admin perms
        if (sender instanceof Player p) {
            if (!p.hasPermission("tcgui.admin.company.delete")) {
                if (!companyAccessor.checkIfOwner(args[2], ((Player) sender).getUniqueId().toString())) {
                    returnError(sender, "You do not own that company");
                    return false;
                }
            }
        }

        return true;
    }
    @Override
    protected void execute(CommandSender sender, String[] args) throws SQLException {
        companyAccessor.deleteCompany(args[2]);
        sender.sendMessage(ChatColor.GREEN + "Successfully deleted company \"" + args[2] + "\"");
    }
    @Override
    public void handle(CommandSender sender, String[] args) {
        if (!checkSync(sender, args)) {
            return;
        }
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            try {
                if (!checkAsync(sender, args)) {
                    return;
                }
                execute(sender, args);
            } catch (SQLException e) {
                getPlugin().reportSqlError(sender, e);
            }
        });
    }
}
