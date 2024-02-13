package com.dnamaster10.traincartsticketshop.commands.tabcompleters;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public abstract class SubCommandCompleter {
    protected abstract boolean checkPermission(Player p, String command);
    public abstract List<String> onTabComplete(CommandSender sender, String[] args);
}
