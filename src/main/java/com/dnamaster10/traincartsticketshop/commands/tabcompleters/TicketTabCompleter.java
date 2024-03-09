package com.dnamaster10.traincartsticketshop.commands.tabcompleters;

import com.dnamaster10.traincartsticketshop.commands.tabcompleters.ticket.TicketCreateTabCompleter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class TicketTabCompleter extends ArgumentCompleter {
    private static final List<String> ARGS1;
    static {
        ARGS1 = new ArrayList<>();
        ARGS1.add("create");
        ARGS1.add("setDisplayName");
        ARGS1.add("setTraincartsTicket");
    }

    private boolean checkPermission(Player p, String command) {
        //Returns boolean indicating whether a player has permission
        //to run the specific sub-command
        return p.hasPermission("traincartsticketshop.ticket." + command.toLowerCase());
    }

    @Override
    public List<String> getCompletions(CommandSender sender, String[] args) {
        //Check that sub-command hasn't already been entered
        if (args.length > 2) {
            return null;
        }

        List<String> subCommands = StringUtil.copyPartialMatches(args[1].toLowerCase(), ARGS1, new ArrayList<>());

        //Return sub-command matches
        if (!(sender instanceof Player)) {
            return subCommands;
        }

        //Check permissions
        subCommands.removeIf(s -> !checkPermission((Player) sender, s));
        return subCommands;
    }

    @Override
    protected List<String> getNextArgumentCompletions(CommandSender sender, String[] args) {
        switch (args[1].toLowerCase()) {
            case "create" -> {
                return new TicketCreateTabCompleter().getCompletions(sender, args);
            }
        }
        return new ArrayList<>();
    }
}
