package com.dnamaster10.traincartsticketshop.commands.tabcompleters.ticket;

import com.dnamaster10.traincartsticketshop.commands.tabcompleters.ArgumentCompleter;
import com.dnamaster10.traincartsticketshop.util.Traincarts;
import com.dnamaster10.traincartsticketshop.util.Utilities;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * The argument completer used for the /tshop ticket create command.
 */
public class TicketCreateTabCompleter extends ArgumentCompleter {
    //Example command: /tshop ticket create <tc ticket name> <optional display name>
    @Override
    public List<String> getCompletions(CommandSender sender, String[] args) {
        if (sender instanceof Player p && !p.hasPermission("traincartsticketshop.ticket.create")) return new ArrayList<>();
        if (args.length > 3) return getNextArgumentCompletions(sender, args);

        List<String> partialNameCompletions = Traincarts.getPartialTicketNameCompletions(args[2]);
        Utilities.quoteSpacedStrings(partialNameCompletions);
        return partialNameCompletions;
    }

    @Override
    protected List<String> getNextArgumentCompletions(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }
}
