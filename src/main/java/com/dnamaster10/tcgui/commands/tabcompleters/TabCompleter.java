package com.dnamaster10.tcgui.commands.tabcompleters;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TabCompleter implements org.bukkit.command.TabCompleter {
    private static final List<String> ARGS0;
    static {
        ARGS0 = new ArrayList<>();
        ARGS0.add("gui");
        ARGS0.add("ticket");
    }
    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        //Handle first sub-command
        if (args.length < 2) {
            return StringUtil.copyPartialMatches(args[0], ARGS0, new ArrayList<>());
        }

        //Select an appropriate tab completer for the given sub-command
        switch (args[0]) {
            case "gui" -> {
                if (args.length < 3) {
                    return (new GuiTabCompleter().onTabComplete(commandSender, args));
                }
                //If entering more sub-commands
                switch (args[1]) {
                    case "editors" -> {
                        return (new GuiEditorsTabCompleter().onTabComplete(commandSender, args));
                    }
                }
            }
            case "ticket" -> {
                return (new TicketTabCompleter().onTabComplete(commandSender, args));
            }
        }
        return null;
    }
}
