package com.dnamaster10.traincartsticketshop.commands.commandhandlers;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.sql.SQLException;

public abstract class AsyncCommandHandler extends CommandHandler {
    protected abstract boolean checkAsync(CommandSender sender, String[] args) throws SQLException;
    protected abstract void execute(CommandSender sender, String[] args) throws SQLException;
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
