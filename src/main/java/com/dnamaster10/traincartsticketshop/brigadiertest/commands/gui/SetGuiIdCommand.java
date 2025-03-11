package com.dnamaster10.traincartsticketshop.brigadiertest.commands.gui;

import com.dnamaster10.traincartsticketshop.brigadiertest.argumenttypes.GuiNameArgumentType;
import com.dnamaster10.traincartsticketshop.brigadiertest.commands.TicketShopCommand;
import com.dnamaster10.traincartsticketshop.brigadiertest.suggestions.GuiNameSuggestionProvider;
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

public class SetGuiIdCommand implements TicketShopCommand {
    @Override
    public LiteralCommandNode<CommandSourceStack> getRootNode() {
        return Commands.literal("setId")
                .requires(ctx -> {
                    if (ctx.getExecutor() instanceof Player player) {
                        return player.hasPermission("traincartsticketshop.gui.setid") || player.hasPermission("traincartsticketshop.admin.gui.setid");
                    }
                    return true;
                }).then(Commands.argument("id", new GuiNameArgumentType()).suggests(GuiNameSuggestionProvider::getSetIdSuggestions)
                        .then(Commands.argument("new id", new GuiNameArgumentType())
                                .executes(this::execute))).build();
    }

    @Override
    public int execute(CommandContext<CommandSourceStack> ctx) {
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            GuiDataAccessor guiAccessor = new GuiDataAccessor();
            String guiName = StringArgumentType.getString(ctx, "id");
            String newGuiName = StringArgumentType.getString(ctx, "new id");
            CommandSender commandSender = ctx.getSource().getSender();

            if (!guiAccessor.checkGuiByName(guiName)) {
                Component component = MiniMessage.miniMessage().deserialize("<red>Gui \"" + guiName + "\" does not exist.");
                commandSender.sendMessage(component);
                return;
            }

            GuiDatabaseObject guiDatabaseObject = guiAccessor.getGuiByName(guiName);

            if (ctx.getSource().getExecutor() instanceof Player player) {
                if (!player.hasPermission("traincartsticketshop.admin.gui.setid")) {
                    if (!guiDatabaseObject.ownerUuid().equals(player.getUniqueId().toString())) {
                        Component component = MiniMessage.miniMessage().deserialize("<red>You must be the owner of a Gui to change its ID.");
                        player.sendMessage(component);
                        return;
                    }
                }
            }

            if (guiAccessor.checkGuiByName(newGuiName)) {
                Component component = MiniMessage.miniMessage().deserialize("<red>A gui with the ID \"" + newGuiName + "\" already exists.");
                commandSender.sendMessage(component);
                return;
            }

            try {
                guiAccessor.updateGuiName(guiDatabaseObject.id(), newGuiName);
            } catch (ModificationException e) {
                getPlugin().handleSqlException(e);
                return;
            }

            Component component = MiniMessage.miniMessage().deserialize("<green>Successfully changed ID from \"" + guiName + "\" to \"" + newGuiName + "\".");
            commandSender.sendMessage(component);

            if (newGuiName.length() >= 15) {
                component = MiniMessage.miniMessage().deserialize("<yellow>Warning: Guis with IDs longer than 15 characters in length may not fit on a sign.");
                commandSender.sendMessage(component);
            }
        });
        return Command.SINGLE_SUCCESS;
    }
}
