package com.dnamaster10.traincartsticketshop.brigadier.commands.gui;

import com.dnamaster10.traincartsticketshop.brigadier.argumenttypes.DisplayNameArgumentType;
import com.dnamaster10.traincartsticketshop.brigadier.argumenttypes.GuiNameArgumentType;
import com.dnamaster10.traincartsticketshop.brigadier.commands.TicketShopCommand;
import com.dnamaster10.traincartsticketshop.brigadier.suggestions.GuiNameSuggestionProvider;
import com.dnamaster10.traincartsticketshop.util.Utilities;
import com.dnamaster10.traincartsticketshop.util.database.accessors.GuiDataAccessor;
import com.dnamaster10.traincartsticketshop.util.database.databaseobjects.GuiDatabaseObject;
import com.dnamaster10.traincartsticketshop.util.exceptions.ModificationException;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static com.dnamaster10.traincartsticketshop.TraincartsTicketShop.getPlugin;

public class RenameGuiCommand implements TicketShopCommand {
    @Override
    public LiteralCommandNode<CommandSourceStack> getRootNode() {
        return Commands.literal("rename")
                .requires(ctx -> {
                    if (ctx.getExecutor() instanceof Player player) {
                        return player.hasPermission("traincartsticketshop.gui.rename") || player.hasPermission("traincartsticketshop.admin.gui.rename");
                    }
                    return true;
                })
                .then(Commands.argument("id", new GuiNameArgumentType()).suggests(GuiNameSuggestionProvider::getRenameSuggestions)
                        .then(Commands.argument("new name", new DisplayNameArgumentType())
                                .executes(this::execute))).build();
    }

    @Override
    public int execute(CommandContext<CommandSourceStack> ctx) {
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            String guiName = StringArgumentType.getString(ctx, "id");
            String colouredDisplayName = StringArgumentType.getString(ctx, "new name");

            Component component = Utilities.parseColour(colouredDisplayName);
            String rawName = Utilities.stripColour(component);
            colouredDisplayName = Utilities.componentToString(component);

            GuiDataAccessor guiAccessor = new GuiDataAccessor();
            CommandSender sender = ctx.getSource().getSender();

            if (!guiAccessor.checkGuiByName(guiName)) {
                Component message = MiniMessage.miniMessage().deserialize("<red>Gui \"" + guiName + "\" does not exist.");
                sender.sendMessage(message);
                return;
            }

            GuiDatabaseObject guiDatabaseObject = guiAccessor.getGuiByName(guiName);

            if (ctx.getSource().getExecutor() instanceof Player player) {
                if (!player.hasPermission("traincartsticketshop.admin.gui.rename")) {
                    if (!guiDatabaseObject.ownerUuid().equals(player.getUniqueId().toString())) {
                        Component message = MiniMessage.miniMessage().deserialize("<red>You must be the owner of a Gui to change its name.");
                        player.sendMessage(message);
                        return;
                    }
                }
            }

            try {
                guiAccessor.updateGuiDisplayName(guiDatabaseObject.id(), colouredDisplayName, rawName);
            } catch (ModificationException e) {
                getPlugin().handleSqlException(e);
                return;
            }

            Component message = MiniMessage.miniMessage().deserialize("<green>Successfully renamed Gui to \"")
                    .append(component)
                    .append(MiniMessage.miniMessage().deserialize("<green>\""));
            ctx.getSource().getSender().sendMessage(message);
        });
        return Command.SINGLE_SUCCESS;
    }
}
