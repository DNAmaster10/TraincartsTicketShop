package com.dnamaster10.traincartsticketshop.commands.tabcompleters;

import org.bukkit.command.CommandSender;

import java.util.List;

public abstract class ArgumentCompleter {
    public abstract List<String> getCompletions(CommandSender sender, String[] args);
    protected abstract List<String> getNextArgumentCompletions(CommandSender sender, String[] args);
}
