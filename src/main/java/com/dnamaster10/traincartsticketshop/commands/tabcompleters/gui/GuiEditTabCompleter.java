package com.dnamaster10.traincartsticketshop.commands.tabcompleters.gui;

import com.dnamaster10.traincartsticketshop.commands.tabcompleters.ArgumentCompleter;
import com.dnamaster10.traincartsticketshop.util.database.AccessorFactory;
import com.dnamaster10.traincartsticketshop.util.database.accessorinterfaces.GuiAccessor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GuiEditTabCompleter extends ArgumentCompleter {
    //Example command: /tshop gui edit <gui name>
    private boolean checkPermissions(Player player) {
        return player.hasPermission("traincartsticketshop.gui.edit") || player.hasPermission("traincartsticketshop.admin.gui.edit");
    }

    @Override
    public List<String> getCompletions(CommandSender sender, String[] args) {
        if (args.length > 3) return getNextArgumentCompletions(sender, args);
        if (sender instanceof Player p && !checkPermissions(p)) return new ArrayList<>();

        GuiAccessor guiAccessor = AccessorFactory.getGuiAccessor();
        return guiAccessor.getPartialNameMatches(args[2]);
    }

    @Override
    protected List<String> getNextArgumentCompletions(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }
}
