package com.dnamaster10.traincartsticketshop.commands.commandhandlers;

import com.dnamaster10.traincartsticketshop.util.Utilities;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

public abstract class SyncCommandHandler extends CommandHandler{
    protected abstract void execute(CommandSender sender, String[] args);
    @Override
    public void handle(CommandSender sender, String[] args) {
        String[] filteredArgs = Utilities.concatenateQuotedStrings(args);
        if (!checkSync(sender, filteredArgs)) {
            return;
        }
        execute(sender, filteredArgs);
    }
}
