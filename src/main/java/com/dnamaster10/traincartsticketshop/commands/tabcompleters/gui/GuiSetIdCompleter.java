package com.dnamaster10.traincartsticketshop.commands.tabcompleters.gui;

import com.dnamaster10.traincartsticketshop.commands.tabcompleters.ArgumentCompleter;
import com.dnamaster10.traincartsticketshop.util.newdatabase.accessors.GuiDataAccessor;
import com.dnamaster10.traincartsticketshop.util.newdatabase.databaseobjects.GuiDatabaseObject;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class GuiSetIdCompleter extends ArgumentCompleter {
    //Example command: /tshop gui rename <gui name> <new name>
    private boolean checkPermissions(Player player) {
        return player.hasPermission("traincartsticketshop.gui.setid") || player.hasPermission("traincartsticketshop.admin.gui.setid");
    }

    @Override
    public List<String> getCompletions(CommandSender sender, String[] args) {
        if (sender instanceof Player p && !checkPermissions(p)) return new ArrayList<>();
        if (args.length > 3) return getNextArgumentCompletions(sender, args);

        GuiDataAccessor guiAccessor = new GuiDataAccessor();
        if (!(sender instanceof Player player) || player.hasPermission("traincartsticketshop.admin.gui.setid")) {
            return guiAccessor.getPartialNameMatches(args[2]);
        }

        //Get guis editable by this player
        List<GuiDatabaseObject> editableGuis = guiAccessor.getGuisEditableBy(player.getUniqueId().toString());
        List<String> editableGuiNames = editableGuis.stream().map(GuiDatabaseObject::name).toList();
        return StringUtil.copyPartialMatches(args[2], editableGuiNames, new ArrayList<>());
    }

    @Override
    protected List<String> getNextArgumentCompletions(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }
}
