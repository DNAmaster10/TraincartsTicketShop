package com.dnamaster10.tcgui.commands.commandhandlers;

import com.dnamaster10.tcgui.TraincartsGui;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.regex.Pattern;

public abstract class CommandHandler {
    //Extends exception is used for the checkAsync method and execute method
    //which may throw an SQL exception if accessing the database.
    private static final Pattern STRING_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]+$");
    //For synchronous command checks (E.g. syntax checks)
    protected abstract boolean checkSync(CommandSender sender, String[] args);
    //For asynchronous command checks (E.g. database checks)
    protected abstract boolean checkAsync(CommandSender sender, String[] args) throws SQLException;
    //Runs the command after all checks are completed
    protected abstract void execute(CommandSender sender, String[] args) throws SQLException;
    //Runs appropriate checks before command is executed
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
    public TraincartsGui getPlugin() {
        return TraincartsGui.getPlugin();
    }
    protected void returnError(CommandSender sender, String error) {
        //Returns an error to sender
        if (sender instanceof Player p) {
            p.sendMessage(ChatColor.RED + error);
        }
        else if (sender instanceof ConsoleCommandSender) {
            getPlugin().getLogger().warning(error);
        }
    }
    protected void returnGuiNotFoundError(CommandSender sender, String guiName) {
        //Returns a "No gui with name "x" exists error to the sender
        returnError(sender, "No gui with the name \"" + guiName + "\" exists");
    }
    protected void returnCompanyNotFoundError(CommandSender sender, String companyName) {
        returnError(sender, "No gui with the name \"" + companyName + "\" exists");
    }
    protected boolean checkStringFormat(String input) {
        //Checks that a string only contains letters, numbers, underscores and dashes
        return STRING_PATTERN.matcher(input).matches();
    }
    protected boolean checkGuiNameSyntax(String guiName) {
        return guiName.length() <= 25 && guiName.length() >= 3 && checkStringFormat(guiName);
    }
    protected boolean checkCompanyNameSyntax(String companyName) {
        return companyName.length() <= 25 && companyName.length() >= 3 && checkStringFormat(companyName);
    }
}
