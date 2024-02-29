package com.dnamaster10.traincartsticketshop.commands.tabcompleters;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class LinkTabCompleter extends SubCommandCompleter {
    private static final List<String> ARGS1;
    static {
        ARGS1 = new ArrayList<>();
        ARGS1.add("create");
        ARGS1.add("setDisplayName");
        ARGS1.add("setDestinationPage");
    }

    @Override
    protected boolean checkPermission(Player p, String command) {
        return p.hasPermission("traincartsticketshop.link." + command.toLowerCase());
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        //If sub-command has already been entered, return final args
        if (args.length > 2) {
            return null;
        }
        //Get sub-command matches
        List<String> subCommands = StringUtil.copyPartialMatches(args[1], ARGS1, new ArrayList<>());

        //If sender isn't player, return
        if (!(sender instanceof Player)) {
            return subCommands;
        }

        //Else check permissions
        subCommands.removeIf(s -> !checkPermission((Player) sender, s));
        return subCommands;
    }
}
