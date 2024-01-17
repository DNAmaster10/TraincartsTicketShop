package com.dnamaster10.tcgui.commands.tabcompleters;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class GuiEditorsTabCompleter extends SubCommandCompleter {
    private static final List<String> ARGS2;
    static {
        ARGS2 = new ArrayList<>();
        ARGS2.add("add");
        ARGS2.add("list");
        ARGS2.add("remove");
        ARGS2.add("removeall");
    }
    @Override
    protected boolean checkPermission(Player p, String command) {
        return p.hasPermission("tcgui.gui.editors." + command);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        //Check that the sub-command hasn't already been entered
        if (args.length > 3) {
            return null;
        }
        //Get sub-command matches
        List<String> subCommands = StringUtil.copyPartialMatches(args[2], ARGS2, new ArrayList<>());

        //If sender isn't player, return
        if (!(sender instanceof Player)) {
            return subCommands;
        }

        //Else, check permissions for all sub-commands. Remove sub-command if player has no permission to use it
        subCommands.removeIf(s -> !checkPermission((Player) sender, s));
        return subCommands;
    }
}
