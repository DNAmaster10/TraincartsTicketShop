package com.dnamaster10.traincartsticketshop.commands.tabcompleters;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import java.util.ArrayList;
import java.util.List;

public class GuiTabCompleter extends SubCommandCompleter {
    private static final List<String> ARGS1;
    static {
        ARGS1 = new ArrayList<>();
        ARGS1.add("addEditor");
        ARGS1.add("checkGuiInfo");
        ARGS1.add("create");
        ARGS1.add("delete");
        ARGS1.add("edit");
        ARGS1.add("listEditors");
        ARGS1.add("open");
        ARGS1.add("removeEditor");
        ARGS1.add("rename");
        ARGS1.add("searchLinkers");
        ARGS1.add("searchTickets");
        ARGS1.add("setDisplayName");
        ARGS1.add("transfer");
    }

    @Override
    protected boolean checkPermission(Player p, String command) {
        return p.hasPermission("traincartsticketshop.gui." + command.toLowerCase()) || p.hasPermission("traincartsticketshop.admin.gui." + command.toLowerCase());
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        //Check that sub-command hasn't already been entered
        if (args.length > 2) {
            return null;
        }
        //Return sub-command matches
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
