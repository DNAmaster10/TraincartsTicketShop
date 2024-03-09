package com.dnamaster10.traincartsticketshop.commands.tabcompleters.gui;

import com.dnamaster10.traincartsticketshop.commands.tabcompleters.ArgumentCompleter;
import com.dnamaster10.traincartsticketshop.util.database.AccessorFactory;
import com.dnamaster10.traincartsticketshop.util.database.accessorinterfaces.GuiAccessor;
import com.dnamaster10.traincartsticketshop.util.database.accessorinterfaces.PlayerAccessor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GuiRemoveEditorTabCompleter extends ArgumentCompleter {
    //Example command: /tshop gui removeEditor <player accessor> <gui name>
    private boolean checkPermissions(Player player) {
        return player.hasPermission("traincartsticketshop.gui.removeeditor") || player.hasPermission("traincartsticketshop.admin.gui.removeeditor");
    }
    @Override
    public List<String> getCompletions(CommandSender sender, String[] args) {
        if (sender instanceof Player p && !checkPermissions(p)) return new ArrayList<>();
        if (args.length > 3) return getNextArgumentCompletions(sender, args);

        //TODO return only editors registered to this gui
        PlayerAccessor playerAccessor = AccessorFactory.getPlayerAccessor();
        return playerAccessor.getPartialUsernameMatches(args[2]);
    }

    @Override
    protected List<String> getNextArgumentCompletions(CommandSender sender, String[] args) {
        GuiAccessor guiAccessor = AccessorFactory.getGuiAccessor();
        return guiAccessor.getPartialNameMatches(args[3]);
    }
}
