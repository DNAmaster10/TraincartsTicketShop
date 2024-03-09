package com.dnamaster10.traincartsticketshop.commands.tabcompleters.gui;

import com.dnamaster10.traincartsticketshop.commands.tabcompleters.ArgumentCompleter;
import com.dnamaster10.traincartsticketshop.util.database.AccessorFactory;
import com.dnamaster10.traincartsticketshop.util.database.accessorinterfaces.GuiAccessor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GuiTransferTabCompleter extends ArgumentCompleter {
    //Example command: /tshop gui transfer <gui name> <player name>
    private boolean checkPermissions(Player player) {
        return player.hasPermission("traincartsticketshop.gui.transfer") || player.hasPermission("traincartsticketshop.admin.gui.transfer");
    }

    @Override
    public List<String> getCompletions(CommandSender sender, String[] args) {
        if (sender instanceof Player p && !checkPermissions(p)) return new ArrayList<>();
        if (args.length > 3) return getNextArgumentCompletions(sender, args);

        GuiAccessor guiAccessor = AccessorFactory.getGuiAccessor();
        return guiAccessor.getPartialNameMatches(args[2]);
    }

    @Override
    protected List<String> getNextArgumentCompletions(CommandSender sender, String[] args) {
        return null;
    }
}
