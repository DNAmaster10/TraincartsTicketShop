package com.dnamaster10.tcgui.commands.tabcompleters;

import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class TicketTabCompleter extends SubCommandCompleter {
    private static final List<String> ARGS1;
    static {
        ARGS1 = new ArrayList<>();
        ARGS1.add("create");
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        //Check that sub-command hasn't already been entered
        if (args.length > 2) {
            return null;
        }
        //Return sub-command matches
        return StringUtil.copyPartialMatches(args[1], ARGS1, new ArrayList<>());
    }
}
