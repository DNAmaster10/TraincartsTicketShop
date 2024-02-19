package com.dnamaster10.traincartsticketshop.commands.commandhandlers;

import org.bukkit.command.CommandSender;

public abstract class SyncCommandHandler extends CommandHandler{
    protected abstract void execute(CommandSender sender, String[] args);
    @Override
    public void handle(CommandSender sender, String[] args) {
        if (!checkSync(sender, args)) {
            return;
        }
        execute(sender, args);
    }
}
