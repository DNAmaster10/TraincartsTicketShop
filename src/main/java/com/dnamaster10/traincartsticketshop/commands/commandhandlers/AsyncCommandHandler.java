package com.dnamaster10.traincartsticketshop.commands.commandhandlers;

import com.dnamaster10.traincartsticketshop.util.Utilities;
import com.dnamaster10.traincartsticketshop.util.exceptions.ModificationException;
import com.dnamaster10.traincartsticketshop.util.exceptions.QueryException;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

import static com.dnamaster10.traincartsticketshop.TraincartsTicketShop.getPlugin;

public abstract class AsyncCommandHandler extends CommandHandler {
    protected abstract boolean checkAsync(CommandSender sender, String[] args) throws QueryException, ModificationException;
    protected abstract void execute(CommandSender sender, String[] args) throws QueryException, ModificationException;
    @Override
    public void handle(CommandSender sender, String[] args) {
        String[] filteredArgs = Utilities.concatenateQuotedStrings(args);
        if (!checkSync(sender, filteredArgs)) {
            return;
        }
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            try {
                if (!checkAsync(sender, filteredArgs)) {
                    return;
                }
                execute(sender, filteredArgs);
            } catch (QueryException | ModificationException e) {
                getPlugin().handleSqlException(sender, e);
            }
        });
    }
}
