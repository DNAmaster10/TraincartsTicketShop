package com.dnamaster10.traincartsticketshop.commands.commandhandlers;

import com.dnamaster10.traincartsticketshop.util.exceptions.DMLException;
import com.dnamaster10.traincartsticketshop.util.exceptions.DQLException;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.sql.SQLException;

public abstract class AsyncCommandHandler extends CommandHandler {
    protected abstract boolean checkAsync(CommandSender sender, String[] args) throws DQLException, DMLException;
    protected abstract void execute(CommandSender sender, String[] args) throws DQLException, DMLException;
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
            } catch (DQLException | DMLException e) {
                getPlugin().handleSqlException(sender, e);
            }
        });
    }
}
