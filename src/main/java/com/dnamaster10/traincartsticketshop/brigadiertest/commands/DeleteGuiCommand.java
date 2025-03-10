package com.dnamaster10.traincartsticketshop.brigadiertest.commands;

import com.dnamaster10.traincartsticketshop.brigadiertest.argumenttypes.GuiNameArgumentType;
import com.dnamaster10.traincartsticketshop.brigadiertest.suggestions.GuiNameSuggestionProvider;
import com.dnamaster10.traincartsticketshop.objects.guis.ConfirmGuiDeleteGui;
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
import org.bukkit.entity.Player;

import static com.dnamaster10.traincartsticketshop.TraincartsTicketShop.getPlugin;

public class DeleteGuiCommand implements TicketShopCommand {
    @Override
    public LiteralCommandNode<CommandSourceStack> getRootNode() {
        return Commands.literal("delete")
                .requires(ctx -> {
                    if (ctx.getExecutor() instanceof Player player) {
                        return player.hasPermission("traincartsticketshop.gui.delete") ||
                                player.hasPermission("traincartsticketshop.admin.gui.delete");
                    }
                    return true;
                }).then(Commands.argument("id", new GuiNameArgumentType()).suggests(GuiNameSuggestionProvider::getDeleteSuggestions)
                        .executes(this::execute)).build();
    }

    @Override
    public int execute(CommandContext<CommandSourceStack> ctx) {
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            String guiName = StringArgumentType.getString(ctx, "id");
            GuiDataAccessor guiAccessor = new GuiDataAccessor();

            if (!guiAccessor.checkGuiByName(guiName)) {
                Component component = MiniMessage.miniMessage().deserialize("<red>Gui \"" + guiName + "\" does not exist.");
                ctx.getSource().getSender().sendMessage(component);
                return;
            }

            GuiDatabaseObject gui = guiAccessor.getGuiByName(guiName);

            if (ctx.getSource().getExecutor() instanceof  Player player) {
                if (!player.hasPermission("traincartsticketshop.admin.gui.delete")) {
                    if (!gui.ownerUuid().equals(player.getUniqueId().toString())) {
                        Component component = MiniMessage.miniMessage().deserialize("<red>You do not own that Gui.");
                        player.sendMessage(component);
                        return;
                    }
                }

                getPlugin().getGuiManager().openNewSession(player);
                ConfirmGuiDeleteGui newGui = new ConfirmGuiDeleteGui(player, gui.id());
                newGui.open();
                return;
            }

            try {
                guiAccessor.deleteGui(gui.id());
            } catch (ModificationException e) {
                getPlugin().handleSqlException(e);
                return;
            }

            Component component = MiniMessage.miniMessage().deserialize("<green>Successfully deleted Gui \"" + guiName + "\".");
            ctx.getSource().getSender().sendMessage(component);
        });
        return Command.SINGLE_SUCCESS;
    }
}
