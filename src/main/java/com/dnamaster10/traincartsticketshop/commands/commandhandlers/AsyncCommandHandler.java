package com.dnamaster10.traincartsticketshop.commands.commandhandlers;

import com.dnamaster10.traincartsticketshop.util.exceptions.ModificationException;
import com.dnamaster10.traincartsticketshop.util.exceptions.QueryException;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import static com.dnamaster10.traincartsticketshop.TraincartsTicketShop.getPlugin;

public abstract class AsyncCommandHandler extends CommandHandler {
    protected abstract boolean checkAsync(CommandSender sender, String[] args) throws QueryException, ModificationException;
    protected abstract void execute(CommandSender sender, String[] args) throws QueryException, ModificationException;
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
            } catch (QueryException | ModificationException e) {
                getPlugin().handleSqlException(sender, e);
            }
        });
    }
}
