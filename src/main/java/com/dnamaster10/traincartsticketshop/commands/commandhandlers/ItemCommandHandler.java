package com.dnamaster10.traincartsticketshop.commands.commandhandlers;

import org.bukkit.command.CommandSender;

public abstract class ItemCommandHandler extends CommandHandler {
    //For commands which alter the players inventory
    protected void returnWrongItemError(CommandSender sender, String correctItem) {
        returnError(sender, "You must be holding a " + correctItem + " in your main hand");
    }
}
