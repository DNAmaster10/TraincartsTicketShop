package com.dnamaster10.tcgui.commands.commandhandlers.company;

import com.dnamaster10.tcgui.commands.commandhandlers.CommandHandler;
import com.dnamaster10.tcgui.util.database.CompanyAccessor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class CompanyCreateCommandHandler extends CommandHandler<SQLException> {
    //Example command: /tcgui company create <company name>
    CompanyAccessor companyAccessor;
    @Override
    protected boolean checkSync(CommandSender sender, String[] args) {
        //Check config
        if (!getPlugin().getConfig().getBoolean("AllowCompanyCreate")) {
            returnError(sender, "Company creation is disabled on this server");
            return false;
        }

        //Check perms and if player
        if (!(sender instanceof Player p)) {
            returnError(sender, "Command must be executed by a player");
            return false;
        }
        if (!p.hasPermission("tcgui.company.create")) {
            returnError(sender, "You do not have permission to perform that action");
            return false;
        }

        //Check syntax
        if (args.length < 3) {
            returnError(sender, "Missing argument(s): /tcgui company create <company name>");
            return false;
        }
        if (args.length > 3) {
            returnError(sender, "Invalid sub-command \"" + args[3] + "\"");
            return false;
        }
        if (args[2].length() < 3) {
            returnError(sender, "Company names cannot be less than 3 characters in length");
            return false;
        }
        if (args[2].length() > 20) {
            returnError(sender, "Company names cannot be more than 20 characters in length");
            return false;
        }
        if (!checkStringFormat(args[2])) {
            returnError(sender, "Company names can only contain characters Aa to Zz, numbers, underscores and dashes");
            return false;
        }

        return true;
    }
    @Override
    protected boolean checkAsync(CommandSender sender, String[] args) throws SQLException {
        //Check that a company with that name doesn't already exist
        companyAccessor = new CompanyAccessor();
        if (companyAccessor.checkCompanyByName(args[2])) {
            returnError(sender, "A company with the name \"" + args[2] + "\" already exists");
            return false;
        }
        return true;
    }
    @Override
    protected void execute(CommandSender sender, String[] args) throws SQLException {
        companyAccessor.addCompany(args[2], ((Player) sender).getUniqueId().toString());
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
