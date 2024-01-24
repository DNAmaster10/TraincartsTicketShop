package com.dnamaster10.tcgui.commands.tabcompleters;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class EditorTabCompleter extends SubCommandCompleter {
    private static final List<String> ARGS1;
    static {
        ARGS1 = new ArrayList<>();
        ARGS1.add("add");
        ARGS1.add("list");
        ARGS1.add("remove");
        ARGS1.add("removeall");
    }
    @Override
    protected boolean checkPermission(Player p, String command) {
        return p.hasPermission("tcgui.gui.editor." + command);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        //Check that the sub-command hasn't already been entered
        if (args.length > 2) {
            return null;
        }
        //Get sub-command matches
        List<String> subCommands = StringUtil.copyPartialMatches(args[1], ARGS1, new ArrayList<>());

        //If sender isn't player, return
        if (!(sender instanceof Player)) {
            return subCommands;
        }

        //Else, check permissions for all sub-commands. Remove sub-command if player has no permission to use it
        subCommands.removeIf(s -> !checkPermission((Player) sender, s));
        return subCommands;
    }
}
