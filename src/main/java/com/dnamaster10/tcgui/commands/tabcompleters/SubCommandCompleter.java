package com.dnamaster10.tcgui.commands.tabcompleters;

import org.bukkit.command.CommandSender;

import java.util.List;

public abstract class SubCommandCompleter {
    public abstract List<String> onTabComplete(CommandSender sender, String[] args);
}
